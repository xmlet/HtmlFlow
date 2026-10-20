package htmlflow;

import org.xmlet.htmlapifaster.MfeConfigurationBuilder;

public final class HtmlMfeConfig extends AbstractMfeConfig {

    private HtmlMfeConfig(Builder builder) {
        super(builder);
    }

    public static non-sealed class Builder
        extends AbstractMfeConfig
        implements MfeConfigurationBuilder {

        @Override
        public Builder setMfeUrlResource(String s) {
            this.mfeUrlResource = s;
            return this;
        }

        @Override
        public Builder setMfeName(String s) {
            this.mfeName = s;
            return this;
        }

        @Override
        public Builder setMfeListeningEventName(String s) {
            this.mfeListeningEventName = s;
            return this;
        }

        @Override
        public Builder setMfeTriggersEventName(String s) {
            this.mfeTriggersEventName = s;
            return this;
        }

        @Override
        public Builder setMfeElementName(String s) {
            this.mfeElementName = s;
            return this;
        }

        @Override
        public Builder setMfeScriptUrl(String s) {
            this.mfeScriptUrl = s;
            return this;
        }

        @Override
        public Builder setMfeStylingUrl(String s) {
            this.mfeStylingUrl = s;
            return this;
        }

        @Override
        public Builder setMfeSharedStylingUrl(String s) {
            this.mfeSharedStylingUrl = s;
            return this;
        }

        @Override
        public Builder setMfeScriptIntegrity(String s) {
            this.mfeScriptIntegrity = s;
            return this;
        }

        @Override
        public Builder setMfeStreamingData(boolean s) {
            this.isMfeStreamingData = s;
            return this;
        }

        public HtmlMfeConfig build() {
            if (mfeUrlResource == null || mfeUrlResource.isBlank()) {
                throw new IllegalStateException("mfeUrlResource is required");
            }
            if (mfeName == null || mfeName.isBlank()) {
                throw new IllegalStateException("mfeName is required");
            }
            return new HtmlMfeConfig(this);
        }
    }
}
