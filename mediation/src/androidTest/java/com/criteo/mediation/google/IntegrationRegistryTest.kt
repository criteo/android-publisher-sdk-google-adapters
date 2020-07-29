package com.criteo.mediation.google

import com.criteo.publisher.integration.Integration
import com.criteo.publisher.integration.IntegrationRegistry
import com.criteo.publisher.mock.MockedDependenciesRule
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

class IntegrationRegistryTest {

  @Rule
  @JvmField
  val mockedDependenciesRule = MockedDependenciesRule()

  @Inject
  private lateinit var integrationRegistry: IntegrationRegistry

  @Test
  fun profileId_WithThisAdapterInClasspath_DetectItAutomatically() {
    val profileId = integrationRegistry.profileId

    assertThat(profileId).isEqualTo(Integration.ADMOB_MEDIATION.profileId)
  }

}