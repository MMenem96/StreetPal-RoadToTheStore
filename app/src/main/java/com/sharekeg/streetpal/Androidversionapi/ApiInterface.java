package com.sharekeg.streetpal.Androidversionapi;


import com.sharekeg.streetpal.Login.LoginCredentialsWithFB;
import com.sharekeg.streetpal.Registration.ConfirmationCode;
import com.sharekeg.streetpal.Registration.TrustedContact;
import com.sharekeg.streetpal.Registration.UserPhoto;
import com.sharekeg.streetpal.Settings.ChangePassword;
import com.sharekeg.streetpal.authentication.Result;
import com.sharekeg.streetpal.forgotpassword.ForgotPassword;
import com.sharekeg.streetpal.forgotpassword.UserEmailForForgetPassword;
import com.sharekeg.streetpal.safeplace.UserSituation;
import com.sharekeg.streetpal.safeplace.nearbyplaceutil.Example;
import com.sharekeg.streetpal.userinfoforeditingprofile.UsersInfoForEditingProfile;
import com.sharekeg.streetpal.Login.LoginCredentials;
import com.sharekeg.streetpal.userinfoforlogin.UserInfoForLogin;
import com.sharekeg.streetpal.userinfoforsignup.UserInfoForSignup;
import com.sharekeg.streetpal.userinfoforsignup.UserInfoForSignupFromFB;


import okhttp3.ResponseBody;
import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * Created by MMenem on 2/22/2017.
 */

public interface ApiInterface


{

    //Login

    @POST("authenticate")
    Call<Result> loginWithCredentials(@Body LoginCredentials data);

    //Login with fb

    @POST("authenticate/fb")
    Call<Result> loginWithCredentialsWithFb(@Body LoginCredentialsWithFB data);


    //Register a new user
    @POST("user")
    Call<Result> insertUserinfo(@Body UserInfoForSignup usersInfoForSignUp);

    //Register a new user with fb
    @POST("user")
    Call<Result> insertUserinfoFromFB(@Body UserInfoForSignupFromFB userInfoForSignupFromFB);

    //Get Current User Info
    @GET("me")
    Call<UserInfoForLogin> getUser();


    // Add or Edit trusted Contact
    @POST("me/trusted")
    Call<ResponseBody> addTrustedContact(@Body TrustedContact newTrustedContact);

    //Delete trusted contact
    @POST("me/trusted")
    Call<ResponseBody> deleteTrustedContact();

    //Get trusted contact details
    @GET("me/trusted")
    Call<TrustedContact> getTrustedContactDetails();

    //Edit password
    @POST("me/pass")
    Call<ResponseBody> changePassword(@Body ChangePassword data);


    //Send  confirmation code to the server
    @POST("me/email-verify/respond")
    Call<ResponseBody> sendCode(@Body ConfirmationCode data);

    //Resend the confirmation code to the user
    @POST("me/email-verify/request")
    Call<ResponseBody> ResendCode();

    //Get the nearby places with user location and specific key
    @GET("api/place/nearbysearch/json?sensor=true&key=AIzaSyDCMt_NIZiZO-sSCFiKdeaqEqtgqcrTDec")
    Call<Example> getNearbyPlaces(@Query("type") String type, @Query("location") String location, @Query("radius") int radius);


    // Send user situation
    @POST("request-help")
    Call<ResponseBody> sendUserSituation(@Body UserSituation data);

    //Edit User Info
    @POST("me")
    Call<ResponseBody> editCurrentUser(@Body UsersInfoForEditingProfile usersInfoForEditingProfile);


    //Upload profile photo
    @POST("me/photo")
    Call<ResponseBody> uploadPhoto(@Body UserPhoto userPhoto);

    //Get profile photo
    @GET("me/photo")
    Call<ResponseBody> getUserPhoto();

    //Send Email to reset user password
    @POST("me/pass-reset/request")
    Call<ResponseBody> sendEmailToResetPassword(@Body UserEmailForForgetPassword userEmailForForgetPassword);

    //Request a new Password
    @POST("me/pass-reset/respond")
    Call<ResponseBody> sendRequestToResetPassword(@Body ForgotPassword forgotPassword);


    //Submit new user password

}
