package se.fredin.fxkcamel.externallinks.bean;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import se.fredin.fxkcamel.jobengine.bean.JobEngineBean;

@CsvRecord(separator = ";", generateHeaderColumns = true, skipFirstLine = true)
public class ItemExternalLinks implements JobEngineBean {

    @DataField(pos = 1, columnName = "Artikelnummer")
    private String articleNumber;

    @DataField(pos = 2, columnName = "Type")
    private String type;

    @DataField(pos = 3, columnName = "Quality")
    private String quality;

    @DataField(pos = 4, columnName = "UNC Path")
    private String uncPath;

    private boolean delete;

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public String getSentToWeb() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getUncPath() {
        return uncPath;
    }

    public void setUncPath(String uncPath) {
        this.uncPath = uncPath;
    }

    public String getType() {
        return type;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    @Override
    public String getId() {
        return getArticleNumber();
    }
}
