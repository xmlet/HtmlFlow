package htmlflow;

import org.xmlet.htmlapifaster.MfeConfiguration;
import org.xmlet.htmlapifaster.MfeConfigurationBuilder;


public final class HtmlMfeConfig implements MfeConfiguration {

    private final String mfeUrlResource;
    private final String mfeName;
    private final String mfeListeningEventName;
    private final String mfeTriggersEventName;
    private final String mfeElementName;
    private final String mfeScriptUrl;
    private final String mfeScriptIntegrity;
    private final String mfeStylingUrl;
    private final String mfeSharedStylingUrl;
    private final boolean isMfeStreamingData;

    private HtmlMfeConfig(Builder builder) {
        this.mfeUrlResource = builder.mfeUrlResource;
        this.mfeName = builder.mfeName;
        this.mfeListeningEventName = builder.mfeListeningEventName;
        this.mfeTriggersEventName = builder.mfeTriggersEventName;
        this.mfeElementName = builder.mfeElementName;
        this.mfeScriptUrl = builder.mfeScriptUrl;
        this.mfeStylingUrl = builder.mfeStylingUrl;
        this.mfeSharedStylingUrl = builder.mfeSharedStylingUrl;
        this.mfeScriptIntegrity = builder.mfeScriptIntegrity;
        this.isMfeStreamingData = builder.isMfeStreamingData;
    }

    @Override public String getMfeUrlResource()        { return mfeUrlResource; }
    @Override public String getMfeName()               { return mfeName; }
    @Override public String getMfeElementName()         { return mfeElementName; }
    @Override public String getMfeListeningEventName()  { return mfeListeningEventName; }
    @Override public String getMfeTriggerEventName()    { return mfeTriggersEventName; }
    @Override public String getMfeScriptUrl()           { return mfeScriptUrl; }
    @Override public String getMfeStylingUrl()          { return mfeStylingUrl; }
    @Override public String getMfeSharedStylingUrl()    { return mfeSharedStylingUrl; }
    @Override public String getMfeScriptIntegrity()     { return mfeScriptIntegrity;}
    @Override public boolean isMfeStreamingData()       {return isMfeStreamingData;}


    public static class Builder implements MfeConfigurationBuilder {
        private String mfeUrlResource;
        private String mfeName;
        private String mfeListeningEventName;
        private String mfeTriggersEventName;
        private String mfeElementName = "micro-frontend";
        private String mfeScriptUrl;
        private String mfeScriptIntegrity;
        private String mfeStylingUrl;
        private String mfeSharedStylingUrl;
        private boolean isMfeStreamingData;

        @Override public Builder setMfeUrlResource(String s)        { this.mfeUrlResource = s; return this; }
        @Override public Builder setMfeName(String s)               { this.mfeName = s; return this; }
        @Override public Builder setMfeListeningEventName(String s) { this.mfeListeningEventName = s; return this; }
        @Override public Builder setMfeTriggersEventName(String s)  { this.mfeTriggersEventName = s; return this; }
        @Override public Builder setMfeElementName(String s)        { this.mfeElementName = s; return this; }
        @Override public Builder setMfeScriptUrl(String s)          { this.mfeScriptUrl = s; return this; }
        @Override public Builder setMfeStylingUrl(String s)         { this.mfeStylingUrl = s; return this; }
        @Override public Builder setMfeSharedStylingUrl(String s)   { this.mfeSharedStylingUrl = s; return this; }
        @Override public Builder setMfeScriptIntegrity(String s)    { this.mfeScriptIntegrity = s; return this;}
        @Override public Builder setMfeStreamingData(boolean s)     { this.isMfeStreamingData = s; return this; }

        @Override public String getMfeUrlResource()        { return mfeUrlResource; }
        @Override public String getMfeName()               { return mfeName; }
        @Override public String getMfeElementName()         { return mfeElementName; }
        @Override public String getMfeListeningEventName()  { return mfeListeningEventName; }
        @Override public String getMfeTriggerEventName()    { return mfeTriggersEventName; }
        @Override public String getMfeScriptUrl()           { return mfeScriptUrl; }
        @Override public String getMfeStylingUrl()          { return mfeStylingUrl; }
        @Override public String getMfeSharedStylingUrl()    { return mfeSharedStylingUrl; }
        @Override public String getMfeScriptIntegrity()     { return mfeScriptIntegrity;}
        @Override public boolean isMfeStreamingData()       { return isMfeStreamingData; }

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
