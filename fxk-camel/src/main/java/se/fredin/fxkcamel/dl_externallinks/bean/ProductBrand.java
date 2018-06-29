package se.fredin.fxkcamel.dl_externallinks.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import se.fredin.fxkcamel.jobengine.bean.FxKBean;

@CsvRecord(separator = "\\|", generateHeaderColumns = true, skipFirstLine = true, quoting = true, allowEmptyStream = true)
public class ProductBrand implements FxKBean {

    @JsonIgnore
    @DataField(pos = 1, columnName = "Product no")
    private String productNo;

    @DataField(pos = 2, columnName = "ClassificationIdentifier")
    private String classificationIdentifier;

    @DataField(pos = 3, columnName = "ClassificationType")
    private String classificationType;

    @DataField(pos = 4, columnName = "ClassificationName")
    private String classificationName;

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getClassificationIdentifier() {
        return classificationIdentifier;
    }

    public void setClassificationIdentifier(String classificationIdentifier) {
        this.classificationIdentifier = classificationIdentifier;
    }

    public String getClassificationType() {
        return classificationType;
    }

    public void setClassificationType(String classificationType) {
        this.classificationType = classificationType;
    }

    public String getClassificationName() {
        return classificationName;
    }

    public void setClassificationName(String classificationName) {
        this.classificationName = classificationName;
    }

    @Override
    public String getId() {
        return this.productNo;
    }

}
