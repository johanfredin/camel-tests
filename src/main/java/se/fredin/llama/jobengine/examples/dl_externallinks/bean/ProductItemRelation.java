package se.fredin.llama.jobengine.examples.dl_externallinks.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import se.fredin.llama.jobengine.bean.FxKBean;

@CsvRecord(separator = "\\|", generateHeaderColumns = true, skipFirstLine = true, quoting = true, allowEmptyStream = true)
public class ProductItemRelation implements FxKBean {

    @JsonIgnore
    @DataField(pos = 1, columnName = "Product no")
    private String productNo;

    @DataField(pos = 2, columnName = "Artikelnummer")
    private String itemNo;

    @DataField(pos = 3, columnName = "Sekvens")
    private String sequence;

    @DataField(pos = 4, columnName = "PIMCatalogs")
    private String pimCatalogs;

    @DataField(pos = 5, columnName = "HPM Status")
    private String hpmStatus;

    @DataField(pos = 6, columnName = "M3-status")
    private String m3Status;

    @DataField(pos = 7, columnName = "Webshop")
    private String webshop;

    @DataField(pos = 8, columnName = "Anskaffn grupp")
    private String anskaffnGroup;

    @DataField(pos = 9, columnName = "UNSPSC")
    private String unspsc;

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getPimCatalogs() {
        return pimCatalogs;
    }

    public void setPimCatalogs(String pimCatalogs) {
        this.pimCatalogs = pimCatalogs;
    }

    public String getHpmStatus() {
        return hpmStatus;
    }

    public void setHpmStatus(String hpmStatus) {
        this.hpmStatus = hpmStatus;
    }

    public String getM3Status() {
        return m3Status;
    }

    public void setM3Status(String m3Status) {
        this.m3Status = m3Status;
    }

    public String getWebshop() {
        return webshop;
    }

    public void setWebshop(String webshop) {
        this.webshop = webshop;
    }

    public String getAnskaffnGroup() {
        return anskaffnGroup;
    }

    public void setAnskaffnGroup(String anskaffnGroup) {
        this.anskaffnGroup = anskaffnGroup;
    }

    public String getUnspsc() {
        return unspsc;
    }

    public void setUnspsc(String unspsc) {
        this.unspsc = unspsc;
    }

    @Override
    public String getId() {
        return this.productNo;
    }

}
