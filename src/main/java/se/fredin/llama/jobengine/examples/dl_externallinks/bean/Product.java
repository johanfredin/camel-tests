package se.fredin.llama.jobengine.examples.dl_externallinks.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import se.fredin.llama.jobengine.bean.FxKBean;

import java.util.List;

@CsvRecord(separator = "\\|", generateHeaderColumns = true, skipFirstLine = true, quoting = true, allowEmptyStream = true)
public class Product implements FxKBean {

    @DataField(pos = 1, columnName = "Product no")
    private String productNo;

    @DataField(pos = 2, columnName = "Sprak")
    private String lang;

    @DataField(pos = 3, columnName = "Produktnamn")
    private String name;

    @DataField(pos = 4, columnName = "Produkttext")
    private String text;

    private List<ProductBrand> brands;
    private List<ProductAsset> assets;
    private List<ProductItemRelation> items;
    private List<ProductLabel> labels;

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<ProductBrand> getBrands() {
        return brands;
    }

    public void setBrands(List<ProductBrand> brands) {
        this.brands = brands;
    }

    public List<ProductAsset> getAssets() {
        return assets;
    }

    public void setAssets(List<ProductAsset> assets) {
        this.assets = assets;
    }

    public List<ProductItemRelation> getItems() {
        return items;
    }

    public void setItems(List<ProductItemRelation> items) {
        this.items = items;
    }

    public List<ProductLabel> getLabels() {
        return labels;
    }

    public void setLabels(List<ProductLabel> labels) {
        this.labels = labels;
    }

    @JsonIgnore
    @Override
    public String getId() {
        return this.productNo;
    }

}
