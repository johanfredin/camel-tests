package se.fredin.fxkcamel.dl_externallinks.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import se.fredin.fxkcamel.jobengine.bean.FxKBean;

@CsvRecord(separator = "\\|", generateHeaderColumns = true, skipFirstLine = true, quoting = true, allowEmptyStream = true)
public class ProductAsset extends FxKBean<String> {

    @JsonIgnore
    @DataField(pos = 1, columnName = "Product no")
    private String productNo;

    @DataField(pos = 2, columnName = "Language-attachments")
    private String languageAttachments;

    @DataField(pos = 3, columnName = "Language-texts")
    private String languageTexts;

    @DataField(pos = 4, columnName = "Type")
    private String type;

    @DataField(pos = 5, columnName = "Quality")
    private String quality;

    @DataField(pos = 6, columnName = "Name")
    private String name;

    @DataField(pos = 7, columnName = "Description")
    private String description;

    @DataField(pos = 8, columnName = "UNCPath")
    private String uncPath;


    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getLanguageAttachments() {
        return languageAttachments;
    }

    public void setLanguageAttachments(String languageAttachments) {
        this.languageAttachments = languageAttachments;
    }

    public String getLanguageTexts() {
        return languageTexts;
    }

    public void setLanguageTexts(String languageTexts) {
        this.languageTexts = languageTexts;
    }

    public String getType() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUncPath() {
        return uncPath;
    }

    public void setUncPath(String uncPath) {
        this.uncPath = uncPath;
    }

    @Override
    public String getId() {
        return this.productNo;
    }
}
