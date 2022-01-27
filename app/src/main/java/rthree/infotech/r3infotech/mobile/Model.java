package rthree.infotech.r3infotech.mobile;

/**
 * Created by USER on 10/01/2017.
 */

public class Model {
    private static Model model=null;
    private Model()
    {

    }
    public static Model getInstance()
    {
        if(model==null)
        {
            model=new Model();
            return  model;
        }
        return model;
    }

    public String getConnectioPath() {
        return ConnectioPath;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    String Company="";
    public void setConnectioPath(String connectioPath) {
        ConnectioPath = connectioPath;
    }

    String ConnectioPath="";
    public String getReceiptRefNo() {
        return receiptRefNo;
    }

    public void setReceiptRefNo(String receiptRefNo) {
        this.receiptRefNo = receiptRefNo;
    }

    String receiptRefNo="";
    public String getUrl_address() {
        return url_address;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getCompanyAddress() {
        return CompanyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        CompanyAddress = companyAddress;
    }

    public String getCompanyGSTIN() {
        return CompanyGSTIN;
    }

    public void setCompanyGSTIN(String companyGSTIN) {
        CompanyGSTIN = companyGSTIN;
    }

    String CompanyName="";

    String CompanyAddress="";
    String CompanyGSTIN="";

    public String getCompanyContact() {
        return CompanyContact;
    }

    public void setCompanyContact(String companyContact) {
        CompanyContact = companyContact;
    }

    String CompanyContact="";
    public void setUrl_address(String url_address) {
        this.url_address = url_address;
    }

    String url_address="";
    public double getSubAmount() {
        return subAmount;
    }

    public void setSubAmount(double subAmount) {
        this.subAmount = subAmount;
    }

    double subAmount;

    public double getDiscountedAmount() {
        return discountedAmount;
    }

    public void setDiscountedAmount(double discountedAmount) {
        this.discountedAmount = discountedAmount;
    }

    double discountedAmount=0;
    public String getTodaysDate() {
        return todaysDate;
    }

    public void setTodaysDate(String todaysDate) {
        this.todaysDate = todaysDate;
    }
    String todaysDate="";
    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    String fromDate="";


    public String getTodaysDateNew() {
        return todaysDateNew;
    }

    public void setTodaysDateNew(String todaysDateNew) {
        this.todaysDateNew = todaysDateNew;
    }

    String todaysDateNew="";
    public double getGstAmount() {
        return GstAmount;
    }

    public void setGstAmount(double gstAmount) {
        GstAmount = gstAmount;
    }

    double GstAmount;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSalesman() {
        return salesman;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    private String username;
    private String salesman;
    private String party;

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    private String dbname;

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    private String serial;

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    private  String OrderNumber;
}
