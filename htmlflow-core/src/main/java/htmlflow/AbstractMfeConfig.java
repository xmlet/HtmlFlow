package htmlflow;

import org.xmlet.htmlapifaster.MfeConfiguration;

/**
 * Shared state and accessors of {@link HtmlMfeConfig} and its {@link HtmlMfeConfig.Builder}.
 */
abstract sealed class AbstractMfeConfig
    implements MfeConfiguration
    permits HtmlMfeConfig, HtmlMfeConfig.Builder {

    protected String mfeUrlResource;
    protected String mfeName;
    protected String mfeListeningEventName;
    protected String mfeTriggersEventName;
    protected String mfeElementName = "micro-frontend";
    protected String mfeScriptUrl;
    protected String mfeScriptIntegrity;
    protected String mfeStylingUrl;
    protected String mfeSharedStylingUrl;
    protected boolean isMfeStreamingData;

    AbstractMfeConfig() {}

    AbstractMfeConfig(AbstractMfeConfig src) {
        this.mfeUrlResource = src.mfeUrlResource;
        this.mfeName = src.mfeName;
        this.mfeListeningEventName = src.mfeListeningEventName;
        this.mfeTriggersEventName = src.mfeTriggersEventName;
        this.mfeElementName = src.mfeElementName;
        this.mfeScriptUrl = src.mfeScriptUrl;
        this.mfeScriptIntegrity = src.mfeScriptIntegrity;
        this.mfeStylingUrl = src.mfeStylingUrl;
        this.mfeSharedStylingUrl = src.mfeSharedStylingUrl;
        this.isMfeStreamingData = src.isMfeStreamingData;
    }

    @Override
    public String getMfeUrlResource() {
        return mfeUrlResource;
    }

    @Override
    public String getMfeName() {
        return mfeName;
    }

    @Override
    public String getMfeElementName() {
        return mfeElementName;
    }

    @Override
    public String getMfeListeningEventName() {
        return mfeListeningEventName;
    }

    @Override
    public String getMfeTriggerEventName() {
        return mfeTriggersEventName;
    }

    @Override
    public String getMfeScriptUrl() {
        return mfeScriptUrl;
    }

    @Override
    public String getMfeStylingUrl() {
        return mfeStylingUrl;
    }

    @Override
    public String getMfeSharedStylingUrl() {
        return mfeSharedStylingUrl;
    }

    @Override
    public String getMfeScriptIntegrity() {
        return mfeScriptIntegrity;
    }

    @Override
    public boolean isMfeStreamingData() {
        return isMfeStreamingData;
    }
}
