package se.fredin.llama.examples.dl_externallinks.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import se.fredin.llama.bean.LlamaBean;

@CsvRecord(separator = "\\|", generateHeaderColumns = true, skipFirstLine = true, quoting = true, allowEmptyStream = true)
public class ProductLabel implements LlamaBean {

    @JsonIgnore
    @DataField(pos = 1, columnName = "Product no")
    private String productNo;

    @DataField(pos = 2, columnName = "Structure group identifier")
    private String structureGroupId;

    @DataField(pos = 3, columnName = "Brand")
    private String brand;

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getStructureGroupId() {
        return structureGroupId;
    }

    public void setStructureGroupId(String structureGroupId) {
        this.structureGroupId = structureGroupId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }


    @Override
    public String getId() {
        return this.productNo;
    }
}
