package com.criteo.mediation.google;

import com.criteo.publisher.Criteo;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

class CriteoHelper {

  /**
   * The Criteo SDK is a singleton that contains some cached data.
   * In order to have isolated test scenario, those cached data should be clean.
   * However, this practice should stay exceptional and should never be done by publishers.
   * Hence, no public API should be developed for that.
   * Also, the Criteo.init method should keep creating only one instance because publishers
   * may call it many times.
   * That's why this is solved with reflection by putting null into the singleton instance holder.
   * Because SDK release artifact is obfuscated by proguard, we cannot directly get the "criteo" field,
   * instead we should loop over the fields and select the static Criteo one.
   *
   * FIXME this is a duplicate from the MoPub adapter tests.
   *  It may also be useful in some tests for the PublisherSDK itself for test isolation.
   *  Maybe could we put this kind of method in a dedicated internal dependency.
   *  Then, test relying on internal introspection could just use that dependency.
   */
  static void givenNotInitializedCriteo() throws ReflectiveOperationException {
    Field[] fields = Criteo.class.getDeclaredFields();
    Field singletonField = null;
    for (Field field : fields) {
      if (Criteo.class.equals(field.getType()) && Modifier.isStatic(field.getModifiers())) {
        singletonField = field;
        break;
      }
    }

    if (singletonField == null) {
      throw new IllegalStateException("Criteo singleton was not found");
    }

    singletonField.setAccessible(true);
    singletonField.set(null, null);
  }

}
