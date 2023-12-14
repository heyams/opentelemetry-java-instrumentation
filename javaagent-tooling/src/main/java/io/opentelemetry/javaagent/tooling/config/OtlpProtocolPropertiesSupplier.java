/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.tooling.config;

import com.google.auto.service.AutoService;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;

@AutoService(AutoConfigurationCustomizerProvider.class)
public class OtlpProtocolPropertiesSupplier implements AutoConfigurationCustomizerProvider {

  @Override
  public void customize(AutoConfigurationCustomizer autoConfigurationCustomizer) {
    autoConfigurationCustomizer.addPropertiesSupplier(ConfigurationFile::getProperties);
    autoConfigurationCustomizer.addPropertiesCustomizer(new OtlpProtocolConfigCustomizer());
  }

  @Override
  public int order() {
    // make sure it runs after all the user-provided customizers
    return Integer.MIN_VALUE;
  }
}
