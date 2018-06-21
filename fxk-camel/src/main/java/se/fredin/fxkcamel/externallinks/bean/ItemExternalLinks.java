package se.fredin.fxkcamel.externallinks.bean;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import se.fredin.fxkcamel.jobengine.mock.bean.JobEngineBean;

@CsvRecord(separator = ";", generateHeaderColumns = true, skipFirstLine = true)
public class ItemExternalLinks implements JobEngineBean {

    @DataField(pos = 1, columnName = "Artikelnummer")
    private String articleNumber;

    @DataField(pos = 2, columnName = "Produktbild")
    private String productImage;

    @DataField(pos = 3, columnName = "Tillagningsbild")
    private String cookingImage;

    @DataField(pos = 4, columnName = "Förpackningsbild")
    private String packagingImage;

    @DataField(pos = 5, columnName = "Säkerhetsdatablad")
    private String safetySheet;

    public ItemExternalLinks() {}

    public ItemExternalLinks(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public ItemExternalLinks(String articleNumber, String productImage, String cookingImage, String packagingImage, String safetySheet) {
        this.articleNumber = articleNumber;
        this.productImage = productImage;
        this.cookingImage = cookingImage;
        this.packagingImage = packagingImage;
        this.safetySheet = safetySheet;
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getCookingImage() {
        return cookingImage;
    }

    public void setCookingImage(String cookingImage) {
        this.cookingImage = cookingImage;
    }

    public String getPackagingImage() {
        return packagingImage;
    }

    public void setPackagingImage(String packagingImage) {
        this.packagingImage = packagingImage;
    }

    public String getSafetySheet() {
        return safetySheet;
    }

    public void setSafetySheet(String safetySheet) {
        this.safetySheet = safetySheet;
    }

    @Override
    public String getId() {
        return getArticleNumber();
    }
}
