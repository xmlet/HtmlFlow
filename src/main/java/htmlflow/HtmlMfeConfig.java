package htmlflow;


import org.xmlet.htmlapifaster.MfeConfiguration;

public class HtmlMfeConfig implements MfeConfiguration {
    private String mfeUrlResource;
    private String mfeName;
    private String mfeListeningEventName;
    private String mfeTriggersEventName;
    private String mfeElementName = "micro-frontend";
    private String mfeScriptUrl = null;
    private String mfeStylingUrl = null;
    private boolean isMfeStreamingData;

    public HtmlMfeConfig(){}

    public HtmlMfeConfig(String mfeUrlResource, String mfeName, String mfeListeningEventName, String mfeTriggersEventName, boolean isMfeStreamingData) {
        this.mfeUrlResource = mfeUrlResource;
        this.mfeName = mfeName;
        this.mfeListeningEventName = mfeListeningEventName;
        this.mfeTriggersEventName = mfeTriggersEventName;
        this.isMfeStreamingData = isMfeStreamingData;
    }


    public HtmlMfeConfig(String mfeUrlResource, String mfeName, String mfeListeningEventName, String mfeTriggersEventName, String mfeScriptUrl, String mfeStylingUrl, boolean isMfeStreamingData) {
        this(mfeUrlResource, mfeName, mfeListeningEventName, mfeTriggersEventName, isMfeStreamingData);
        this.mfeScriptUrl = mfeScriptUrl;
        this.mfeStylingUrl = mfeStylingUrl;
    }

    public HtmlMfeConfig(String mfeUrlResource, String mfeName, String mfeListeningEventName, String mfeTriggersEventName, String mfeScriptUrl, String mfeStylingUrl) {
        this(mfeUrlResource, mfeName, mfeListeningEventName, mfeTriggersEventName, false);
        this.mfeScriptUrl = mfeScriptUrl;
        this.mfeStylingUrl = mfeStylingUrl;
    }

    public HtmlMfeConfig(String mfeUrlResource, String mfeName, String mfeElementName, String mfeListeningEventName, String mfeTriggersEventName, String mfeScriptUrl, String mfeStylingUrl, boolean isMfeStreamingData) {
        this(mfeUrlResource, mfeName, mfeListeningEventName, mfeTriggersEventName, mfeScriptUrl, mfeStylingUrl, isMfeStreamingData);
        this.mfeElementName = mfeElementName;
    }

    @Override
    public String getMfeUrlResource() {
        return mfeUrlResource;
    }

    @Override
    public String getMfeName() { return mfeName; }
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
    public boolean isMfeStreamingData() {
        return this.isMfeStreamingData;
    }

    @Override
    public boolean setMfeStreamingData(boolean mfeStreamingData) {
        return this.isMfeStreamingData = mfeStreamingData;
    }

    @Override
    public String setMfeUrlResource(String mfeUrlResource) {
        return this.mfeUrlResource = mfeUrlResource;
    }

    @Override
    public String setMfeName(String mfeName) {
        return this.mfeName = mfeName;
    }

    @Override
    public String setMfeListeningEventName(String mfeListeningEventName) {
        return this.mfeListeningEventName = mfeListeningEventName;
    }

    @Override
    public String setMfeTriggersEventName(String mfeTriggersEventName) {
        return this.mfeTriggersEventName = mfeTriggersEventName;
    }

    @Override
    public String setMfeElementName(String mfeElementName) {
        return this.mfeElementName = mfeElementName;
    }

    @Override
    public String setMfeScriptUrl(String mfeScriptUrl) {
        return this.mfeScriptUrl = mfeScriptUrl;
    }

    @Override
    public String setMfeStylingUrl(String mfeStylingUrl) {
        return this.mfeStylingUrl = mfeStylingUrl;
    }
}
