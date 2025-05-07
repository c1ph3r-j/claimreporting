package com.aki.claimreporting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String Databasename = "ClaimReporting.db";
    public static final String Tokeninformation = "claim_token_info";
    public static final String FireBaseinformation = "Firebase_token_info";
    public static final String Userinformation = "claim_user_info";
    public static final String TermsConditions = "terms_conditions_info";
    public static final String TableNearDear = "tableneardear";
    public static final String Insurerinformation = "insurerinformation";
    public static final String TableClaimOfflineID = "tableclaimofflineid";
    public static final String Vehicleinformation = "Vehicle_info";
    public static final String SMSinformation = "SMS_info";
    public static final String Tablenamelocalimages = "imageinsertlocal";
    public static final String TablenameThirdpartydetails = "imagethirddetails";
    public static final String Tablenamethirdpartlocalimages = "imagethirdlocal";
    public static final String TableClaimStep1 = "claim_step1";
    public static final String TableClaimStep2 = "claim_step2";
    public static final String TableClaimMoreImg = "claimmoreimg";
    public static final String TableRegSteps = "addregsteps";
    public static final String TableClaimSteps = "addclaimsteps";
    public static final String TableTermsConditions = "addtermsconditions";
    public static final String TableDriverInformation = "adddriverinformation";
    public static final String Autoid = "ID";
    public static final String Tokenvalue = "TokenValue";
    public static final String Firebasetokenvalue = "FireBaseTokenvalue";
    public static final String Firstname = "FirstName";
    public static final String Lastname = "LastName";
    public static final String Phoneno = "PhoneNo";
    public static final String CRAuserid = "CRAuserid";
    public static final String CRAPhonenum = "CRAPhonenum";
    public static final String CRAEmailid = "CRAEmailid";
    public static final String Emailid = "EmailId";
    public static final String Regno = "RegNo";
    public static final String Certno = "CertNo";
    public static final String Companyname = "CompanyName";
    public static final String Makeval = "MakeVal";
    public static final String Modelval = "ModelVal";
    public static final String Yearval = "YearVal";
    public static final String Insureddate = "InsuredDate";
    public static final String VehiclerefiD = "VehicleRefID";
    public static final String Covertypeid = "CoverTypeId";
    public static final String Insurancecompanyid = "InsuranceCompanyId";
    public static final String Typeofvechileid = "TypeofVechileId";
    public static final String Typeofvechilname = "TypeofVechileName";
    public static final String CertificateType = "CertificateType";
    public static final String PolicyEndDate = "PolicyEndDate";
    public static final String InsuredName = "InsuredName";
    public static final String InsuredPhoneNo = "InsuredPhoneNo";
    public static final String InsuredMailId = "InsuredMailId";
    public static final String PolicyNumVehicle = "PolicyNumVehicle";
    public static final String InsuredPinNumber = "InsuredPinNumber";
    public static final String Chassisnumber = "ChassisNumber";
    public static final String VechNewRefId = "VechNewRefId";
    public static final String imgattachmentname = "Imgvalattachmentname";
    public static final String imgattachmenttypeid = "Imgvalattachmenttypeid";
    public static final String imgattachmentbyte = "Imgvalattachmentbyte";
    public static final String imgcaridval = "Imgnewcaridval";
    public static final String imgcarnameval = "Imgnewcarnameval";
    public static final String imgcardescription = "Imgnewcardescription";
    public static final String imgmergenamedescr = "Imgnewmergenamedescr";
    public static final String imguniqueID = "ImgnewuniqueID";
    public static final String imgattachmentid = "Imgnewattachmentid";
    public static final String imgregnum = "Imgnewregnum";
    public static final String imgimagenew = "Imgnewimagenew";
    public static final String thirdregno = "ThirdRegno";
    public static final String thirdcertno = "ThirdCertno";
    public static final String thirdwhatypeofvehicle = "ThirdwhaTypeofvehicle";
    public static final String thirdnameowner = "ThirdNameowner";
    public static final String thirdnamedriver = "ThirdNamedriver";
    public static final String thirdaddress = "ThirdAddress";
    public static final String thirdextentdamage = "ThirdExtentdamage";
    public static final String thirdpartyinsurer = "ThirdPartyinsurer";
    public static final String thirdmake = "ThirdMake";
    public static final String thirdmodel = "ThirdModel";
    public static final String thirdyearofman = "ThirdYearofman";
    public static final String thirdinsurancecompany = "ThirdInsurancecompany";
    public static final String thirdnameinsurer = "ThirdNameinsurer";
    public static final String thirdpolicynum = "ThirdPolicynum";
    public static final String thirdmanypersons = "ThirdManyPersons";
    public static final String thirdimgattachmentname = "ThirdImgvalattachmentname";
    public static final String thirdimgattachmenttypeid = "ThirdImgvalattachmenttypeid";
    public static final String thirdimgattachmentbyte = "ThirdImgvalattachmentbyte";
    public static final String thirdimgcaridval = "ThirdImgnewcaridval";
    public static final String thirdimgcarnameval = "ThirdImgnewcarnameval";
    public static final String thirdimgcardescription = "ThirdImgnewcardescription";
    public static final String thirdimgmergenamedescr = "ThirdImgnewmergenamedescr";
    public static final String thirdimguniqueID = "ThirdImgnewuniqueID";
    public static final String thirdimgattachmentid = "ThirdImgnewattachmentid";
    public static final String thirdimgregnum = "ThirdImgnewregnum";
    public static final String thirdimgimagenew = "ThirdImgnewimagenew";
    public static final String TypeofVechileID = "TypeofVechileID";
    public static final String Vehiclerefid = "Vehiclerefid";
    public static final String Registraionno = "RegistraionNo";
    public static final String VehileType = "Vehiletype";
    public static final String TypeofCoverID = "TypeofCoverid";
    public static final String InsuranceCompanyID = "Insurancecompanyid";
    public static final String ClaimType = "ClaimType";
    public static final String HumanInjured = "HumanInjured";
    public static final String CattleInjured = "CattleInjured";
    public static final String ThirdPartyDamage = "ThirdPartyDamage";
    public static final String Claimimgattachmentbyte = "Claimimgattachmentbyte";
    public static final String Claimimgcomments = "Claimimgcomments";
    public static final String Claimimgstolenid = "Claimimgstolenid";
    public static final String Claimaddstep = "Claimaddstep";
    public static final String Termsstep = "Termsstep";
    public static final String Vehicleaddstep = "Vehicleaddstep";
    public static final String ClaimTermsID = "ClaimTermsID";
    public static final String ClaimTermsURL = "ClaimTermsURL";
    public static final String VehicleTermsID = "VehicleTermsID";
    public static final String VehicleTermsURL = "VehicleTermsURL";
    public static final String NearDearExist = "NearDearExist";
    public static final String ClaimOffineID = "ClaimOffineID";
    public static final String InsurerID = "InsurerID";
    public static final String DMVICMemberID = "DMVICMemberID";
    public static final String IMIDSMemberID = "IMIDSMemberID";
    public static final String InsurerName = "InsurerName";
    public static final String SMSID = "SMSID";
    public static final String SMSNUMBER = "SMSNUMBER";
    public static final String DriverUserId = "DriverUserId";
    public static final String DriverName = "DriverName";
    public static final String DriverDLCountry = "DriverDLCountry";
    public static final String DriverDLNum = "DriverDLNum";
    public static final String DriverDLValidFrom = "DriverDLValidFrom";
    public static final String DriverDLValidTill = "DriverDLValidTill";
    public static final String MobileNo = "MobileNo";
    public static final String VehicleIDDriver = "VehicleIDDriver";
    public static final String VehicleIsSelfDriver = "VehicleIsSelfDriver";
    public static final String VehicleDriverCRAID = "VehicleDriverCRAID";
    private FirebaseCrashlytics mCrashlytics;

    public DatabaseHelper(Context context) {
        super(context, Databasename, null, 3);
        mCrashlytics = FirebaseCrashlytics.getInstance();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("create table " + Tokeninformation + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,TokenValue VARCHAR)");
            db.execSQL("create table if not EXISTS " + FireBaseinformation + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,FireBaseTokenvalue VARCHAR)");
            db.execSQL("create table " + Userinformation + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Firstname VARCHAR,CRAuserid VARCHAR,CRAPhonenum VARCHAR,CRAEmailid VARCHAR)");
            db.execSQL("create table " + Vehicleinformation + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Regno VARCHAR,Certno VARCHAR,Companyname VARCHAR,Makeval VARCHAR,Modelval VARCHAR,Yearval VARCHAR,Insureddate VARCHAR,VehiclerefiD VARCHAR,Covertypeid VARCHAR,Insurancecompanyid VARCHAR,Typeofvechileid VARCHAR,Typeofvechilename VARCHAR,Chassisnumber VARCHAR,VechNewRefId VARCHAR,CertificateType VARCHAR,PolicyEndDate VARCHAR,InsuredName VARCHAR,InsuredPhoneNo VARCHAR,InsuredMailId VARCHAR,PolicyNumVehicle VARCHAR,InsuredPinNumber VARCHAR)");
            db.execSQL("create table " + Tablenamelocalimages + " (ImgID INTEGER PRIMARY KEY AUTOINCREMENT,Imgvalattachmentname VARCHAR,Imgvalattachmenttypeid INTEGER,Imgvalattachmentbyte VARCHAR,Imgnewcaridval INTEGER,Imgnewcarnameval VARCHAR,Imgnewcardescription VARCHAR,Imgnewmergenamedescr VARCHAR,ImgnewuniqueID VARCHAR,Imgnewattachmentid INTEGER,Imgnewregnum VARCHAR,Imgnewimagenew VARCHAR)");
            db.execSQL("create table " + TableClaimStep1 + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,TypeofVechileID VARCHAR,Vehiclerefid VARCHAR,RegistraionNo VARCHAR,Vehiletype INTEGER,TypeofCoverid VARCHAR,Insurancecompanyid VARCHAR)");
            db.execSQL("create table " + TableClaimStep2 + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,ClaimType VARCHAR,HumanInjured VARCHAR,CattleInjured VARCHAR,ThirdPartyDamage VARCHAR)");
            db.execSQL("create table " + Tablenamethirdpartlocalimages + " (ThirdImgID INTEGER PRIMARY KEY AUTOINCREMENT,ThirdImgvalattachmentname VARCHAR,ThirdImgvalattachmenttypeid INTEGER,ThirdImgvalattachmentbyte VARCHAR,ThirdImgnewcaridval INTEGER,ThirdImgnewcarnameval VARCHAR,ThirdImgnewcardescription VARCHAR,ThirdImgnewmergenamedescr VARCHAR,ThirdImgnewuniqueID VARCHAR,ThirdImgnewattachmentid INTEGER,ThirdImgnewregnum VARCHAR,ThirdImgnewimagenew VARCHAR)");
            db.execSQL("create table " + TablenameThirdpartydetails + " (ThirdID INTEGER PRIMARY KEY AUTOINCREMENT,ThirdRegno VARCHAR,ThirdCertno VARCHAR,ThirdwhaTypeofvehicle VARCHAR,ThirdNameowner VARCHAR,ThirdNamedriver VARCHAR,ThirdAddress VARCHAR,ThirdExtentdamage VARCHAR,ThirdPartyinsurer VARCHAR,ThirdMake VARCHAR,ThirdModel VARCHAR,ThirdYearofman VARCHAR,ThirdInsurancecompany VARCHAR,ThirdNameinsurer VARCHAR,ThirdPolicynum VARCHAR,ThirdManyPersons VARCHAR)");
            db.execSQL("create table " + TableClaimMoreImg + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Claimimgattachmentbyte VARCHAR,Claimimgcomments VARCHAR,Claimimgstolenid VARCHAR)");
            db.execSQL("create table " + TableRegSteps + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Vehicleaddstep VARCHAR)");
            db.execSQL("create table " + TableClaimSteps + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,Claimaddstep VARCHAR)");
            db.execSQL("create table " + TermsConditions + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,Termsstep VARCHAR)");
            db.execSQL("create table " + Insurerinformation + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,InsurerID VARCHAR,DMVICMemberID VARCHAR,IMIDSMemberID VARCHAR,InsurerName VARCHAR)");
            db.execSQL("create table " + TableNearDear + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,NearDearExist VARCHAR)");
            db.execSQL("create table " + TableClaimOfflineID + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,ClaimOffineID VARCHAR)");
            db.execSQL("create table " + SMSinformation + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,SMSID VARCHAR,SMSNUMBER VARCHAR)");
            db.execSQL("create table " + TableTermsConditions + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,ClaimTermsID VARCHAR,ClaimTermsURL VARCHAR,VehicleTermsID VARCHAR,VehicleTermsURL VARCHAR)");
            db.execSQL("create table " + TableDriverInformation + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,DriverUserId VARCHAR,DriverName VARCHAR,DriverDLCountry VARCHAR,DriverDLNum VARCHAR,DriverDLValidFrom VARCHAR,DriverDLValidTill VARCHAR,MobileNo VARCHAR,VehicleIDDriver VARCHAR,VehicleIsSelfDriver VARCHAR,VehicleDriverCRAID VARCHAR)");

        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + Tokeninformation);
            db.execSQL("DROP TABLE IF EXISTS " + Userinformation);
            db.execSQL("DROP TABLE IF EXISTS " + Vehicleinformation);
            db.execSQL("DROP TABLE IF EXISTS " + Tablenamelocalimages);
            db.execSQL("DROP TABLE IF EXISTS " + TableClaimStep1);
            db.execSQL("DROP TABLE IF EXISTS " + TableClaimStep2);
            db.execSQL("DROP TABLE IF EXISTS " + Tablenamethirdpartlocalimages);
            db.execSQL("DROP TABLE IF EXISTS " + TablenameThirdpartydetails);
            db.execSQL("DROP TABLE IF EXISTS " + TableClaimMoreImg);
            db.execSQL("DROP TABLE IF EXISTS " + TableRegSteps);
            db.execSQL("DROP TABLE IF EXISTS " + TableClaimSteps);
            db.execSQL("DROP TABLE IF EXISTS " + TermsConditions);
            db.execSQL("DROP TABLE IF EXISTS " + TableTermsConditions);
            db.execSQL("DROP TABLE IF EXISTS " + TableDriverInformation);
            db.execSQL("DROP TABLE IF EXISTS " + TableNearDear);
            db.execSQL("DROP TABLE IF EXISTS " + TableClaimOfflineID);
            db.execSQL("DROP TABLE IF EXISTS " + SMSinformation);
            db.execSQL("DROP TABLE IF EXISTS " + Insurerinformation);
            onCreate(db);
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
        }
    }

    public boolean inserttoken(String tokentype) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Tokenvalue, tokentype);
            long result = db.insert(Tokeninformation, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            return false;
        }

    }




    public boolean insertneardear(String nearval) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(NearDearExist, nearval);
            long result = db.insert(TableNearDear, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            return false;
        }

    }


    public boolean insertsendsms(String smsid, String smsnum) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(SMSID, smsid);
            contentValues.put(SMSNUMBER, smsnum);
            long result = db.insert(SMSinformation, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            return false;
        }

    }

    public Cursor getsendsmsdetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + SMSinformation, null);
        return res;
    }

    public void deletesendsmsdata() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + SMSinformation);
        return;
    }


    public Cursor getneardeardetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TableNearDear, null);
        return res;
    }

    public void deleteneardeardata() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TableNearDear);
        return;
    }


    public boolean insertclaimofflineid(String claimofflineid) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ClaimOffineID, claimofflineid);
            long result = db.insert(TableClaimOfflineID, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            return false;
        }

    }


    public Cursor getclaimofflineiddetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TableClaimOfflineID, null);
        return res;
    }

    public void deleteclaimofflineiddata() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TableClaimOfflineID);
        return;
    }


    public boolean inserttermsid(String claimtermsid, String claimtermsurl, String vehicletermsid, String vehicletermsurl) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ClaimTermsID, claimtermsid);
            contentValues.put(ClaimTermsURL, claimtermsurl);
            contentValues.put(VehicleTermsID, vehicletermsid);
            contentValues.put(VehicleTermsURL, vehicletermsurl);
            long result = db.insert(TableTermsConditions, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            return false;
        }

    }

    public Cursor gettermsconditionsdetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TableTermsConditions, null);
        return res;
    }

    public void deletetermsconditionsdata() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TableTermsConditions);
        return;
    }

    public Cursor getTokendetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + Tokeninformation, null);
        return res;
    }

    public void deletetokendata() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Tokeninformation);
        return;
    }


    public boolean insertdriverinfo(String driverid, String drivername, String drivercountry, String driverdl, String drivervalidfrom, String drivervalidto, String drivermobile, String drivervechid, String driverself, String drivercraid) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DriverUserId, driverid);
            contentValues.put(DriverName, drivername);
            contentValues.put(DriverDLCountry, drivercountry);
            contentValues.put(DriverDLNum, driverdl);
            contentValues.put(DriverDLValidFrom, drivervalidfrom);
            contentValues.put(DriverDLValidTill, drivervalidto);
            contentValues.put(MobileNo, drivermobile);
            contentValues.put(VehicleIDDriver, drivervechid);
            contentValues.put(VehicleIsSelfDriver, driverself);
            contentValues.put(VehicleDriverCRAID, drivercraid);
            long result = db.insert(TableDriverInformation, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            return false;
        }

    }


    public Cursor getdriverdetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TableDriverInformation, null);
        return res;
    }

    public Cursor getspecificdriverdetails(String vechid) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TableDriverInformation + " WHERE VehicleIDDriver = " + "'" + vechid + "'", null);
        return res;

    }

    public Cursor getspecificdriverpolicy(String vechid) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TableDriverInformation + " WHERE DriverUserId = " + "'" + vechid + "'", null);
        return res;

    }

    public void deletedriverdetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TableDriverInformation);
        return;
    }


    public boolean insertterms(String terms) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Termsstep, terms);
            long result = db.insert(TermsConditions, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            return false;
        }

    }

    public Cursor getTermsdetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TermsConditions, null);
        return res;
    }

    public void deletetermsdata() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TermsConditions);
        return;
    }

    public boolean insertregstep(String stepval) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Vehicleaddstep, stepval);
            long result = db.insert(TableRegSteps, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            return false;
        }

    }

    public Cursor getregstep() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TableRegSteps, null);
        return res;
    }

    public void deleteregstep() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TableRegSteps);
        return;
    }

    public boolean insertclaimstep(String stepvalclaim) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Claimaddstep, stepvalclaim);
            long result = db.insert(TableClaimSteps, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            return false;
        }

    }

    public Cursor getclaimstep() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TableClaimSteps, null);
        return res;
    }

    public void deleteclaimstep() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TableClaimSteps);
        return;
    }

    public boolean insertclaimmoreimg(String imgvalbyte, String comments, String captureid) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Claimimgattachmentbyte, imgvalbyte);
            contentValues.put(Claimimgcomments, comments);
            contentValues.put(Claimimgstolenid, captureid);
            long result = db.insert(TableClaimMoreImg, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            return false;
        }

    }

    public Cursor getClaimImgmore() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select Claimimgattachmentbyte,Claimimgcomments,Claimimgstolenid from " + TableClaimMoreImg, null);
        return res;
    }

    public void deleteClaimImgmor() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TableClaimMoreImg);
        return;
    }

    public boolean insertfirebasetoken(String fbtokentype) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Firebasetokenvalue, fbtokentype);
            long result = db.insert(FireBaseinformation, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            return false;
        }

    }

    public Cursor getfirebaseTokendetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + FireBaseinformation, null);
        return res;
    }

    public void deletefirebasetokendata() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + FireBaseinformation);
        return;
    }

    public void deleteuserdata() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Userinformation);
        return;
    }

    public boolean insertuserdetails(String firstname, String crauserid, String phnum, String emailid) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Firstname, firstname);
            contentValues.put(CRAuserid, crauserid);
            contentValues.put(CRAPhonenum, phnum);
            contentValues.put(CRAEmailid, emailid);
            long result = db.insert(Userinformation, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            return false;
        }

    }

    public String getUserPhoneNumber() {
        SQLiteDatabase db = this.getReadableDatabase();
        String phoneNumber = null;
        Cursor cursor = null;

        try {
            // Query to select the phone number from the Userinformation table
            String query = "SELECT " + CRAPhonenum + " FROM " + Userinformation + " LIMIT 1";
            cursor = db.rawQuery(query, null);

            // Move to the first row and get the phone number if the cursor is not empty
            if (cursor.moveToFirst()) {
                phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(CRAPhonenum));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            mCrashlytics.recordException(ex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return phoneNumber;
    }

    public boolean insertthirpartyedetails(String regno, String certno, String whaTypeofvehicle, String Nameowner, String Namedriver, String Address, String Extentdamage, String insurer, String Make, String Model, String Yearofman, String Insurancecompany, String Nameinsurer, String Policynum, String ManyPersons) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(thirdregno, regno);
            contentValues.put(thirdcertno, certno);
            contentValues.put(thirdwhatypeofvehicle, whaTypeofvehicle);
            contentValues.put(thirdnameowner, Nameowner);
            contentValues.put(thirdnamedriver, Namedriver);
            contentValues.put(thirdaddress, Address);
            contentValues.put(thirdextentdamage, Extentdamage);
            contentValues.put(thirdpartyinsurer, insurer);
            contentValues.put(thirdmake, Make);
            contentValues.put(thirdmodel, Model);
            contentValues.put(thirdyearofman, Yearofman);
//            contentValues.put(Insurancecompanyid,insurancecompanyid);
//            contentValues.put(Typeofvechileid,typeofvechileid);
//            contentValues.put(Typeofvechilname,typeofvechilname);
            contentValues.put(thirdinsurancecompany, Insurancecompany);
            contentValues.put(thirdnameinsurer, Nameinsurer);
            contentValues.put(thirdpolicynum, Policynum);
            contentValues.put(thirdmanypersons, ManyPersons);
            long result = db.insert(TablenameThirdpartydetails, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            return false;
        }

    }

    public void deletethirdpartydetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TablenameThirdpartydetails);
        return;
    }

    public Cursor getthirdpartydetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TablenameThirdpartydetails, null);
        return res;
    }

    public boolean insertthirdimages(String imgattachmentnameval, Integer imgattachmenttypeidval, String imgattachmentbyteval, Integer imgcaridvalnew, String imgcarnamevalnew, String imgcardescriptionnew, String imgmergenamedescrnew, String imguniqueIDnew, Integer imgattachmentidnew, String imgregnumnew, String imgimagenewest) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(thirdimgattachmentname, imgattachmentnameval);
            contentValues.put(thirdimgattachmenttypeid, imgattachmenttypeidval);
            contentValues.put(thirdimgattachmentbyte, imgattachmentbyteval);
            contentValues.put(thirdimgcaridval, imgcaridvalnew);
            contentValues.put(thirdimgcarnameval, imgcarnamevalnew);
            contentValues.put(thirdimgcardescription, imgcardescriptionnew);
            contentValues.put(thirdimgmergenamedescr, imgmergenamedescrnew);
            contentValues.put(thirdimguniqueID, imguniqueIDnew);
            contentValues.put(thirdimgattachmentid, imgattachmentidnew);
            contentValues.put(thirdimgregnum, imgregnumnew);
            contentValues.put(thirdimgimagenew, imgimagenewest);

            long result = db.insert(Tablenamethirdpartlocalimages, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            return false;
        }


    }

    public Cursor getthirdlocalimages() {

        SQLiteDatabase db = this.getWritableDatabase();

        //  Cursor resimage = db.rawQuery("Select ImgID,Imgvalattachmentname,Imgvalattachmenttypeid,Imgvalattachmentbyte,Imgnewcaridval,Imgnewcarnameval,Imgnewcardescription,Imgnewmergenamedescr,ImgnewuniqueID,Imgnewattachmentid,Imgnewregnum,Imgnewimagenew from "+ Tablenamelocalimages,null);
        Cursor resimage = db.rawQuery("Select ThirdImgnewregnum,ThirdImgnewcarnameval,ThirdImgnewcardescription,ThirdImgnewcaridval from " + Tablenamethirdpartlocalimages, null);
        return resimage;
    }


    public Cursor getdeletethirdlocalalreadyid(String idval, String regnoval) {

        SQLiteDatabase db = this.getWritableDatabase();
        //  Cursor resimage = db.rawQuery("Select ImgID,Imgvalattachmentname,Imgvalattachmenttypeid,Imgvalattachmentbyte,Imgnewcaridval,Imgnewcarnameval,Imgnewcardescription,Imgnewmergenamedescr,ImgnewuniqueID,Imgnewattachmentid,Imgnewregnum,Imgnewimagenew from "+ Tablenamelocalimages,null);
        Cursor resimage = db.rawQuery("Select * from " + Tablenamethirdpartlocalimages + " Where ThirdImgnewcaridval=" + idval + " AND ThirdImgnewregnum='" + regnoval + "'", null);
        return resimage;
    }


    public void deletethirdlocalalreadyid(String idval) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Tablenamethirdpartlocalimages + " WHERE ThirdImgnewcaridval='" + idval + "'");
    }

//    public Cursor getspecifictthirdimages(String regnoval)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor resimage = db.rawQuery("Select ThirdImgvalattachmentbyte from "+ Tablenamethirdpartlocalimages + " WHERE ThirdImgnewregnum=" + regnoval,null);
//        return resimage;
//    }

    public Cursor getthirdmagesgroup(String regnoval) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resimage = db.rawQuery("Select ThirdImgvalattachmentbyte,ThirdImgvalattachmentname,ThirdImgnewuniqueID from " + Tablenamethirdpartlocalimages + " WHERE ThirdImgnewregnum = " + "'" + regnoval + "'", null);
        return resimage;
    }

    public Cursor getthirdgroupdecalration() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resimage = db.rawQuery("Select ThirdImgvalattachmentbyte from " + Tablenamethirdpartlocalimages, null);
        return resimage;
    }

    public Cursor getlocalthirdimageattachment() {


        SQLiteDatabase db = this.getWritableDatabase();
        //  Cursor resimage = db.rawQuery("Select ImgID,Imgvalattachmentname,Imgvalattachmenttypeid,Imgvalattachmentbyte,Imgnewcaridval,Imgnewcarnameval,Imgnewcardescription,Imgnewmergenamedescr,ImgnewuniqueID,Imgnewattachmentid,Imgnewregnum,Imgnewimagenew from "+ Tablenamelocalimages,null);
        Cursor resimage = db.rawQuery("Select ThirdImgnewcarnameval,ThirdImgnewuniqueID,ThirdImgnewcaridval from " + Tablenamethirdpartlocalimages, null);
        return resimage;
    }

    public Cursor getthirdinfo(String regnoval) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resimage = db.rawQuery("Select ThirdPolicynum,ThirdNameowner,ThirdMake,ThirdModel,ThirdYearofman,ThirdRegno,ThirdCertno from " + TablenameThirdpartydetails + " WHERE ThirdRegno = " + "'" + regnoval + "'", null);
        return resimage;
    }

    public void deletethirdlocalimage() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Tablenamethirdpartlocalimages);
        return;
    }


    public Cursor getUserdetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + Userinformation, null);
        return res;
    }

    public void deletevehicledata() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Vehicleinformation);
        return;
    }

    public boolean insertvehicledetails(String regno, String certno, String insuracomp, String make, String model, String year, String dateinsured, String vehiclerefiD, String covertypeid, String chassisnumber, String vechnewid, String certtype, String enddate, String insurename, String insurephno, String insureemail, String policynum, String insuredin) {
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Regno, regno);
            contentValues.put(Certno, certno);
            contentValues.put(Companyname, insuracomp);
            contentValues.put(Makeval, make);
            contentValues.put(Modelval, model);
            contentValues.put(Yearval, year);
            contentValues.put(Insureddate, dateinsured);
            contentValues.put(VehiclerefiD, vehiclerefiD);
            contentValues.put(Covertypeid, covertypeid);
//            contentValues.put(Insurancecompanyid,insurancecompanyid);
//            contentValues.put(Typeofvechileid,typeofvechileid);
//            contentValues.put(Typeofvechilname,typeofvechilname);
            contentValues.put(Chassisnumber, chassisnumber);
            contentValues.put(VechNewRefId, vechnewid);
            contentValues.put(CertificateType, certtype);
            contentValues.put(PolicyEndDate, enddate);
            contentValues.put(InsuredName, insurename);
            contentValues.put(InsuredPhoneNo, insurephno);
            contentValues.put(InsuredMailId, insureemail);
            contentValues.put(PolicyNumVehicle, policynum);
            contentValues.put(InsuredPinNumber, insuredin);
            long result = db.insert(Vehicleinformation, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            return false;
        }

    }

    public Cursor getvehicledetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + Vehicleinformation, null);
        return res;
    }

    public Cursor getspecificvehicledetails(String vechidval) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + Vehicleinformation + " WHERE VehiclerefiD = " + "'" + vechidval + "'", null);
        return res;
    }


    public boolean insertinsurerdetails(String insurerID, String dMVICMemberCompanyID, String iMIDSMemberCompanyID, String insurerName) {
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(InsurerID, insurerID);
            contentValues.put(DMVICMemberID, dMVICMemberCompanyID);
            contentValues.put(IMIDSMemberID, iMIDSMemberCompanyID);
            contentValues.put(InsurerName, insurerName);
            long result = db.insert(Insurerinformation, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            return false;
        }

    }

    public Cursor getinsuererdetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + Insurerinformation, null);
        return res;
    }

    public void deleteinsurerdata() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Insurerinformation);
        return;
    }


    public boolean insertlocalimageattachment(String imgattachmentnameval, Integer imgattachmenttypeidval, String imgattachmentbyteval, Integer imgcaridvalnew, String imgcarnamevalnew, String imgcardescriptionnew, String imgmergenamedescrnew, String imguniqueIDnew, Integer imgattachmentidnew, String imgregnumnew, String imgimagenewest) {
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(imgattachmentname, imgattachmentnameval);
            contentValues.put(imgattachmenttypeid, imgattachmenttypeidval);
            contentValues.put(imgattachmentbyte, imgattachmentbyteval);
            contentValues.put(imgcaridval, imgcaridvalnew);
            contentValues.put(imgcarnameval, imgcarnamevalnew);
            contentValues.put(imgcardescription, imgcardescriptionnew);
            contentValues.put(imgmergenamedescr, imgmergenamedescrnew);
            contentValues.put(imguniqueID, imguniqueIDnew);
            contentValues.put(imgattachmentid, imgattachmentidnew);
            contentValues.put(imgregnum, imgregnumnew);
            contentValues.put(imgimagenew, imgimagenewest);

            long result = db.insert(Tablenamelocalimages, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            return false;
        }


    }

    public Cursor getdeletelocalalreadyid(String idval) {

        SQLiteDatabase db = this.getWritableDatabase();
        //  Cursor resimage = db.rawQuery("Select ImgID,Imgvalattachmentname,Imgvalattachmenttypeid,Imgvalattachmentbyte,Imgnewcaridval,Imgnewcarnameval,Imgnewcardescription,Imgnewmergenamedescr,ImgnewuniqueID,Imgnewattachmentid,Imgnewregnum,Imgnewimagenew from "+ Tablenamelocalimages,null);
        Cursor resimage = db.rawQuery("Select * from " + Tablenamelocalimages + " Where Imgnewcaridval=" + idval, null);
        return resimage;
    }


    public void deletelocalalreadyid(String idval) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Tablenamelocalimages + " WHERE Imgnewcaridval='" + idval + "'");
    }

//    public Cursor deletelocalalreadyid(String idval)
//    {
//
//
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        //  Cursor resimage = db.rawQuery("Select ImgID,Imgvalattachmentname,Imgvalattachmenttypeid,Imgvalattachmentbyte,Imgnewcaridval,Imgnewcarnameval,Imgnewcardescription,Imgnewmergenamedescr,ImgnewuniqueID,Imgnewattachmentid,Imgnewregnum,Imgnewimagenew from "+ Tablenamelocalimages,null);
//        Cursor resimage = db.rawQuery("delete from "+ Tablenamelocalimages + " Where Imgnewcaridval = " + "'" + idval +"'",null);
//        return resimage;
//    }

    public Cursor getinsertloceattachment() {

        SQLiteDatabase db = this.getWritableDatabase();
        //  Cursor resimage = db.rawQuery("Select ImgID,Imgvalattachmentname,Imgvalattachmenttypeid,Imgvalattachmentbyte,Imgnewcaridval,Imgnewcarnameval,Imgnewcardescription,Imgnewmergenamedescr,ImgnewuniqueID,Imgnewattachmentid,Imgnewregnum,Imgnewimagenew from "+ Tablenamelocalimages,null);
        Cursor resimage = db.rawQuery("Select Imgnewcarnameval,Imgnewcardescription,Imgnewcaridval,Imgvalattachmentbyte,ImgnewuniqueID from " + Tablenamelocalimages, null);
        return resimage;
    }

//    public Cursor getlocalimageattachment()
//    {
//
//
//        SQLiteDatabase db = this.getWritableDatabase();
//      //  Cursor resimage = db.rawQuery("Select ImgID,Imgvalattachmentname,Imgvalattachmenttypeid,Imgvalattachmentbyte,Imgnewcaridval,Imgnewcarnameval,Imgnewcardescription,Imgnewmergenamedescr,ImgnewuniqueID,Imgnewattachmentid,Imgnewregnum,Imgnewimagenew from "+ Tablenamelocalimages,null);
//        Cursor resimage = db.rawQuery("Select Imgnewcarnameval,Imgnewcardescription,Imgnewcaridval from "+ Tablenamelocalimages + " GROUP BY Imgnewcaridval",null);
//        return resimage;
//    }

    public Cursor getlocalimageattachment() {

        SQLiteDatabase db = this.getWritableDatabase();
        //  Cursor resimage = db.rawQuery("Select ImgID,Imgvalattachmentname,Imgvalattachmenttypeid,Imgvalattachmentbyte,Imgnewcaridval,Imgnewcarnameval,Imgnewcardescription,Imgnewmergenamedescr,ImgnewuniqueID,Imgnewattachmentid,Imgnewregnum,Imgnewimagenew from "+ Tablenamelocalimages,null);
        Cursor resimage = db.rawQuery("Select Imgnewcarnameval,ImgnewuniqueID,Imgnewcaridval from " + Tablenamelocalimages + " GROUP BY Imgnewcaridval", null);
        return resimage;
    }

    public Cursor getspecificimagesgroup(String idimage) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resimage = db.rawQuery("Select Imgnewimagenew from " + Tablenamelocalimages + " Where Imgnewcaridval=" + idimage, null);
        return resimage;
    }

    public Cursor getalldecalrationimagesgroup() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resimage = db.rawQuery("Select Imgvalattachmentbyte from " + Tablenamelocalimages + " Where Imgnewcaridval", null);
        return resimage;
    }

    public void deletealllocalimage() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Tablenamelocalimages);
        return;
    }

    public boolean insertclaimstep1(String typeofvechileid, String vehiclerefiD, String registraionno, String vehileType, String typeofCoverID, String insurancecompanyid) {
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TypeofVechileID, typeofvechileid);
            contentValues.put(Vehiclerefid, vehiclerefiD);
            contentValues.put(Registraionno, registraionno);
            contentValues.put(VehileType, vehileType);
            contentValues.put(TypeofCoverID, typeofCoverID);
            contentValues.put(InsuranceCompanyID, insurancecompanyid);
            long result = db.insert(TableClaimStep1, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            return false;
        }


    }

    public Cursor getclaimstep1details() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TableClaimStep1, null);
        return res;
    }

    public void deleteclaimstep1data() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TableClaimStep1);
        return;
    }


    public boolean insertclaimstep2(String claimType, String humanInjured, String cattleInjured, String thirdPartyDamage) {
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ClaimType, claimType);
            contentValues.put(HumanInjured, humanInjured);
            contentValues.put(CattleInjured, cattleInjured);
            contentValues.put(ThirdPartyDamage, thirdPartyDamage);
            long result = db.insert(TableClaimStep2, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            mCrashlytics.recordException(ex);
            return false;
        }


    }

    public Cursor getclaimstep2details() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("Select * from " + TableClaimStep2, null);
        return res;
    }

    public void deleteclaimstep2data() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TableClaimStep2);
        return;
    }

}