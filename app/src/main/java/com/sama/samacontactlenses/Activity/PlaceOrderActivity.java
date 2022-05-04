package com.sama.samacontactlenses.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import company.tap.gosellapi.GoSellSDK;
import company.tap.gosellapi.internal.api.callbacks.GoSellError;
import company.tap.gosellapi.internal.api.models.Authorize;
import company.tap.gosellapi.internal.api.models.Charge;
import company.tap.gosellapi.internal.api.models.PhoneNumber;
import company.tap.gosellapi.internal.api.models.SaveCard;
import company.tap.gosellapi.internal.api.models.SavedCard;
import company.tap.gosellapi.internal.api.models.Token;
import company.tap.gosellapi.open.buttons.PayButtonView;
import company.tap.gosellapi.open.controllers.SDKSession;
import company.tap.gosellapi.open.controllers.ThemeObject;
import company.tap.gosellapi.open.delegate.SessionDelegate;
import company.tap.gosellapi.open.enums.AppearanceMode;
import company.tap.gosellapi.open.enums.CardType;
import company.tap.gosellapi.open.enums.TransactionMode;
import company.tap.gosellapi.open.models.CardsList;
import company.tap.gosellapi.open.models.Customer;
import company.tap.gosellapi.open.models.PaymentItem;
import company.tap.gosellapi.open.models.TapCurrency;
import company.tap.gosellapi.open.models.Tax;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.sama.samacontactlenses.Adapter.OrderPlaceAdapter;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.Helper.SettingsManager;
import com.sama.samacontactlenses.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.CART_DETAILS_ID;
import static com.sama.samacontactlenses.Common.Constant.CART_MASTER_ID;
import static com.sama.samacontactlenses.Common.Constant.CATEGORY;
import static com.sama.samacontactlenses.Common.Constant.CURR_CODE;
import static com.sama.samacontactlenses.Common.Constant.LEFT_AXIS;
import static com.sama.samacontactlenses.Common.Constant.LEFT_B_C;
import static com.sama.samacontactlenses.Common.Constant.LEFT_CYL;
import static com.sama.samacontactlenses.Common.Constant.LEFT_EYE;
import static com.sama.samacontactlenses.Common.Constant.LEFT_SPH;
import static com.sama.samacontactlenses.Common.Constant.ORDER_ID;
import static com.sama.samacontactlenses.Common.Constant.POWER;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DESC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_PRICE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_QTY;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_AXIS;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_B_C;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_CYL;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_EYE;
import static com.sama.samacontactlenses.Common.Constant.RIGHT_SPH;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;
import static com.sama.samacontactlenses.Helper.MethodClass.noFormat;

public class PlaceOrderActivity extends AppCompatActivity implements SessionDelegate {
    private RecyclerView recy_view_trend_prod;
    private ArrayList<HashMap<String, String>> arrayList;
    private NestedScrollView scrollView;
    private TextView item_tv, total_price_tv, discount_tv, paybale_amount_tv;
    private Button checkout_btn;
    private LinearLayout discount_lay;

    private TextView name,address,phone;
    private TextView name_1,address_1,phone_1;
    private Button online_btn,cod_btn;
    private Button payment_btn;
    private String order_id="";

    private final int SDK_REQUEST_CODE = 1001;
    private SDKSession sdkSession;
    private PayButtonView payButtonView;
    private SettingsManager settingsManager;
    private ProgressDialog progress;
    String order_total = "";
    LinearLayout wallet_lay,payment_method_lay;
    TextView wallet_tv;
    TextView cod_charge_tv;
    LinearLayout cod_charge_lay;
    public LinearLayout shipping_price_lay;
    public TextView shipping_price_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        MethodClass.set_locale(this);
        setContentView(R.layout.content_placeorder);
        TextView title = findViewById(R.id.title);
        title.setText(getString(R.string.place_order));
        recy_view_trend_prod = findViewById(R.id.recy_view_trend_prod);
        item_tv = findViewById(R.id.item_tv);
        total_price_tv = findViewById(R.id.total_price_tv);
        discount_tv = findViewById(R.id.discount_tv);
        paybale_amount_tv = findViewById(R.id.paybale_amount_tv);
        checkout_btn = findViewById(R.id.checkout_btn);
        scrollView = findViewById(R.id.scrollView);
        discount_lay = findViewById(R.id.discount_lay);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        name_1 = findViewById(R.id.name_1);
        address_1 = findViewById(R.id.address_1);
        phone_1 = findViewById(R.id.phone_1);
        cod_btn=findViewById(R.id.cod_btn);
        online_btn=findViewById(R.id.online_btn);
        payment_btn=findViewById(R.id.payment_btn);
        wallet_lay=findViewById(R.id.wallet_lay);
        wallet_tv=findViewById(R.id.wallet_tv);
        payment_method_lay=findViewById(R.id.payment_method_lay);
        cod_charge_lay=findViewById(R.id.cod_charge_lay);
        cod_charge_tv=findViewById(R.id.cod_charge_tv);
        scrollView.setVisibility(View.GONE);
        shipping_price_lay = findViewById(R.id.shipping_price_lay);
        shipping_price_tv = findViewById(R.id.shipping_price_tv);
        payButtonView = findViewById(R.id.payButtonId);
        Log.e("order_no",getIntent().getStringExtra("order_no"));
        getOrderData();
        payment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });

    }

//Payment Gateway
    /**
     * Integrating SDK.
     */
    private void startSDK(){
        /**
         * Required step.
         * Configure SDK with your Secret API key and App Bundle name registered with tap company.
         */
        configureApp();

        /**
         * Optional step
         * Here you can configure your app theme (Look and Feel).
         */
        configureSDKThemeObject();

        /**
         * Required step.
         * Configure SDK Session with all required data.
         */
        configureSDKSession();

        /**
         * Required step.
         * Choose between different SDK modes
         */
        configureSDKMode();

        /**
         * If you included Tap Pay Button then configure it first, if not then ignore this step.
         */
        initPayButton();
    }

    /**
     * Required step.
     * Configure SDK with your Secret API key and App Bundle name registered with tap company.
     */
    private void configureApp(){
        //Toast.makeText(this, PreferenceManager.getDefaultSharedPreferences(PlaceOrderActivity.this).getString("lang","en"), Toast.LENGTH_SHORT).show();
        GoSellSDK.init(this, "sk_live_GiA5OaDxqwWJs8BK1MIb3o4E","com.sama.samacontactlenses");  // to be replaced by merchant, you can contact tap support team to get you credentials
        GoSellSDK.setLocale(PreferenceManager.getDefaultSharedPreferences(PlaceOrderActivity.this).getString("lang","en"));//  if you dont pass locale then default locale EN will be used
    }

    private void configureSDKThemeObject() {

        ThemeObject.getInstance()

                // set Appearance mode [Full Screen Mode - Windowed Mode]
                .setAppearanceMode(AppearanceMode.WINDOWED_MODE) // **Required**

                .setSdkLanguage(PreferenceManager.getDefaultSharedPreferences(PlaceOrderActivity.this).getString("lang","en")) //if you dont pass locale then default locale EN will be used

                // Setup header font type face **Make sure that you already have asset folder with required fonts**
                .setHeaderFont(Typeface.createFromAsset(getAssets(),"fonts/cairo_regular.ttf"))//**Optional**

                //Setup header text color
                .setHeaderTextColor(getResources().getColor(R.color.black1))  // **Optional**

                // Setup header text size
                .setHeaderTextSize(17) // **Optional**

                // setup header background
                .setHeaderBackgroundColor(getResources().getColor(R.color.french_gray_new))//**Optional**

                // setup card form input font type
                .setCardInputFont(Typeface.createFromAsset(getAssets(),"fonts/cairo_regular.ttf"))//**Optional**

                // setup card input field text color
                .setCardInputTextColor(getResources().getColor(R.color.black))//**Optional**

                // setup card input field text color in case of invalid input
                .setCardInputInvalidTextColor(getResources().getColor(R.color.red))//**Optional**

                // setup card input hint text color
                .setCardInputPlaceholderTextColor(getResources().getColor(R.color.black))//**Optional**

                // setup Switch button Thumb Tint Color in case of Off State
                .setSaveCardSwitchOffThumbTint(getResources().getColor(R.color.gray)) // **Optional**

                // setup Switch button Thumb Tint Color in case of On State
                .setSaveCardSwitchOnThumbTint(getResources().getColor(R.color.colorPrimaryDark)) // **Optional**

                // setup Switch button Track Tint Color in case of Off State
                .setSaveCardSwitchOffTrackTint(getResources().getColor(R.color.gray)) // **Optional**

                // setup Switch button Track Tint Color in case of On State
                .setSaveCardSwitchOnTrackTint(getResources().getColor(R.color.colorPrimary)) // **Optional**

                // change scan icon
                .setScanIconDrawable(getResources().getDrawable(R.drawable.btn_card_scanner_normal)) // **Optional**

                // setup pay button selector [ background - round corner ]
                .setPayButtonResourceId(R.drawable.btn_pay_selector)

                // setup pay button font type face
                .setPayButtonFont(Typeface.createFromAsset(getAssets(),"fonts/cairo_regular.ttf")) // **Optional**

                // setup pay button disable title color
                .setPayButtonDisabledTitleColor(getResources().getColor(R.color.black)) // **Optional**

                // setup pay button enable title color
                .setPayButtonEnabledTitleColor(getResources().getColor(R.color.white)) // **Optional**

                //setup pay button text size
                .setPayButtonTextSize(16) // **Optional**

                // show/hide pay button loader
                .setPayButtonLoaderVisible(true) // **Optional**

                // show/hide pay button security icon
                .setPayButtonSecurityIconVisible(true) // **Optional**

                // set the text on pay button
                .setPayButtonText("PAY NOW") // **Optional**


                // setup dialog textcolor and textsize
                .setDialogTextColor(getResources().getColor(R.color.black1))     // **Optional**
                .setDialogTextSize(17)                // **Optional**

        ;

    }


    /**
     * Configure SDK Session
     */
    private void configureSDKSession() {

        // Instantiate SDK Session
        if(sdkSession==null) sdkSession = new SDKSession();   //** Required **

        // pass your activity as a session delegate to listen to SDK internal payment process follow
        sdkSession.addSessionDelegate(this);    //** Required **

        // initiate PaymentDataSource
        sdkSession.instantiatePaymentDataSource();    //** Required **

        TransactionMode trx_mode = (settingsManager != null) ? settingsManager.getTransactionsMode("key_sdk_transaction_mode") : TransactionMode.PURCHASE;
        sdkSession.setTransactionMode(trx_mode);
        // set transaction currency associated to your account
        sdkSession.setTransactionCurrency(new TapCurrency(PreferenceManager.getDefaultSharedPreferences(PlaceOrderActivity.this).getString("currency_code","AED")));    //** Required **

        // Using static CustomerBuilder method available inside TAP Customer Class you can populate TAP Customer object and pass it to SDK
        sdkSession.setCustomer(getCustomer());    //** Required **

        // Set Total Amount. The Total amount will be recalculated according to provided Taxes and Shipping
        sdkSession.setAmount(new BigDecimal(Double.parseDouble(order_total)));  //** Required **
        Log.e("TEST", "configureSDKSession: " );
        // Set Payment Items array list
        sdkSession.setPaymentItems(new ArrayList<PaymentItem>());// ** Optional ** you can pass empty array list
        Log.e("TEST", "configureSDKSession: " );
        // Set Taxes array list
        sdkSession.setTaxes(new ArrayList<Tax>());// ** Optional ** you can pass empty array list
        Log.e("TEST", "configureSDKSession: " );
        // Set Shipping array list
        sdkSession.setShipping(new ArrayList<>());// ** Optional ** you can pass empty array list
        Log.e("TEST", "configureSDKSession: " );
        // Post URL
        sdkSession.setPostURL("https://app.samalenses.com/"); // ** Optional **
        Log.e("TEST", "configureSDKSession: " );
        // Payment Description
        sdkSession.setPaymentDescription("You are purchasing your product " + (order_id.equals("null") || order_id.isEmpty() ? "000": order_id)  +
                " from Sama Contact Lens through TAP payment. " +
                "Your money is safe with us. If you face any problem feel free to contact us."); //** Optional **

        Log.e("TEST", "configureSDKSession: " );
        // Payment Extra Info
        sdkSession.setPaymentMetadata(new HashMap<>());// ** Optional ** you can pass empty array hash map
        Log.e("TEST", "configureSDKSession: " );
        // Payment Reference
        sdkSession.setPaymentReference(null); // ** Optional ** you can pass null
        Log.e("TEST", "configureSDKSession: " );
        // Payment Statement Descriptor

        // Enable or Disable Saving Card
        sdkSession.isUserAllowedToSaveCard(true); //  ** Required ** you can pass boolean
        Log.e("TEST", "configureSDKSession: " );
        // Enable or Disable 3DSecure
        sdkSession.isRequires3DSecure(true);
        Log.e("TEST", "configureSDKSession: " );
        //Set Receipt Settings [SMS - Email ]
        sdkSession.setReceiptSettings(null); // ** Optional ** you can pass Receipt object or null
        Log.e("TEST", "configureSDKSession: " );
        // Set Authorize Action
        sdkSession.setAuthorizeAction(null); // ** Optional ** you can pass AuthorizeAction object or null
        Log.e("TEST", "configureSDKSession: " );
        sdkSession.setDestination(null); // ** Optional ** you can pass Destinations object or null
        Log.e("TEST", "configureSDKSession: " );
        sdkSession.setMerchantID(null); // ** Optional ** you can pass merchant id or null
        Log.e("TEST", "configureSDKSession: " );
//        sdkSession.setPaymentType("CARD");   //** Merchant can customize payment options [WEB/CARD] for each transaction or it will show all payment options granted to him.
//        Log.e("TEST", "configureSDKSession: " );
//        //sdkSession.setCardType(CardType.CREDIT); // ** Optional ** you can pass which cardType[CREDIT/DEBIT] you want.By default it loads all available cards for Merchant.
//
//        sdkSession.setDefaultCardHolderName(PreferenceManager.getDefaultSharedPreferences(this).getString("name","")); // ** Optional ** you can pass default CardHolderName of the user .So you don't need to type it.
//        Log.e("TEST", "configureSDKSession: " );
//        sdkSession.isUserAllowedToEnableCardHolderName(true); //** Optional ** you can enable/ disable  default CardHolderName .
//        Log.e("TEST", "configureSDKSession: " );
        /**
         * Use this method where ever you want to show TAP SDK Main Screen.
         * This method must be called after you configured SDK as above
         * This method will be used in case of you are not using TAP PayButton in your activity.
         */
        sdkSession.start(this);
    }

    /**
     * Configure SDK Theme
     */
    private void configureSDKMode(){

        /**
         * You have to choose only one Mode of the following modes:
         * Note:-
         *      - In case of using PayButton, then don't call sdkSession.start(this); because the SDK will start when user clicks the tap pay button.
         */
        //////////////////////////////////////////////////////    SDK with UI //////////////////////
        /**
         * 1- Start using  SDK features through SDK main activity (With Tap CARD FORM)
         */
        startSDKWithUI();

    }
    /**
     * Start using  SDK features through SDK main activity
     */
    private void startSDKWithUI() {
        if (sdkSession != null) {
            TransactionMode trx_mode = (settingsManager != null) ? settingsManager.getTransactionsMode("key_sdk_transaction_mode") : TransactionMode.PURCHASE;
            // set transaction mode [TransactionMode.PURCHASE - TransactionMode.AUTHORIZE_CAPTURE - TransactionMode.SAVE_CARD - TransactionMode.TOKENIZE_CARD ]
            sdkSession.setTransactionMode(trx_mode);    //** Required **
            // if you are not using tap button then start SDK using the following call
            //sdkSession.start(this);
        }
    }


    /**
     * Include pay button in merchant page
     */
    private void initPayButton() {


        if (ThemeObject.getInstance().getPayButtonFont() != null)
            payButtonView.setupFontTypeFace(ThemeObject.getInstance().getPayButtonFont());
        if (ThemeObject.getInstance().getPayButtonDisabledTitleColor() != 0 && ThemeObject.getInstance().getPayButtonEnabledTitleColor() != 0)
            payButtonView.setupTextColor(ThemeObject.getInstance().getPayButtonEnabledTitleColor(),
                    ThemeObject.getInstance().getPayButtonDisabledTitleColor());
        if (ThemeObject.getInstance().getPayButtonTextSize() != 0)
            payButtonView.getPayButton().setTextSize(ThemeObject.getInstance().getPayButtonTextSize());
//
        if (ThemeObject.getInstance().isPayButtSecurityIconVisible())
            payButtonView.getSecurityIconView().setVisibility(ThemeObject.getInstance().isPayButtSecurityIconVisible() ? View.VISIBLE : View.INVISIBLE);
        if (ThemeObject.getInstance().getPayButtonResourceId() != 0)
            payButtonView.setBackgroundSelector(ThemeObject.getInstance().getPayButtonResourceId());

        if (sdkSession != null) {
            TransactionMode trx_mode = sdkSession.getTransactionMode();
            if (trx_mode != null) {

                if (TransactionMode.SAVE_CARD == trx_mode || TransactionMode.SAVE_CARD_NO_UI == trx_mode) {
                    payButtonView.getPayButton().setText(getString(company.tap.gosellapi.R.string.save_card));
                } else if (TransactionMode.TOKENIZE_CARD == trx_mode || TransactionMode.TOKENIZE_CARD_NO_UI == trx_mode) {
                    payButtonView.getPayButton().setText(getString(company.tap.gosellapi.R.string.tokenize));
                } else {
                    payButtonView.getPayButton().setText(getString(company.tap.gosellapi.R.string.pay));
                }
            } else {
                configureSDKMode();
            }
            sdkSession.setButtonView(payButtonView, this, SDK_REQUEST_CODE);
        }


    }


    //    //////////////////////////////////////////////////////  List Saved Cards  ////////////////////////

    /**
     * retrieve list of saved cards from the backend.
     */
   /* private void listSavedCards() {
        if (sdkSession != null)
            sdkSession.listAllCards("cus_s4H13120191115x0R12606480", this);
    }*/

//    //////////////////////////////////////////////////////  Overridden section : Session Delegation ////////////////////////

    @Override
    public void paymentSucceed(@NonNull Charge charge) {

        System.out.println("Payment Succeeded : charge status : " + charge.getStatus());
        System.out.println("Payment Succeeded : description : " + charge.getDescription());
        System.out.println("Payment Succeeded : message : " + charge.getResponse().getMessage());
        Log.e("Response", charge.toString() );
        makePay("S",charge.getId(),charge.getResponse().getMessage(),charge.getTransaction().getAuthorizationID());
        System.out.println("##############################################################################");
        if (charge.getCard() != null) {
            System.out.println("Payment Succeeded : first six : " + charge.getCard().getFirstSix());
            System.out.println("Payment Succeeded : last four: " + charge.getCard().getLast4());
            System.out.println("Payment Succeeded : card object : " + charge.getCard().getObject());
            System.out.println("Payment Succeeded : brand : " + charge.getCard().getBrand());
            System.out.println("Payment Succeeded : expiry : " + charge.getCard().getExpiry().getMonth()+"\n"+charge.getCard().getExpiry().getYear());
        }

        System.out.println("##############################################################################");
        if (charge.getAcquirer() != null) {
            System.out.println("Payment Succeeded : acquirer id : " + charge.getAcquirer().getId());
            System.out.println("Payment Succeeded : acquirer response code : " + charge.getAcquirer().getResponse().getCode());
            System.out.println("Payment Succeeded : acquirer response message: " + charge.getAcquirer().getResponse().getMessage());
        }
        System.out.println("##############################################################################");
        if (charge.getSource() != null) {
            System.out.println("Payment Succeeded : source id: " + charge.getSource().getId());
            System.out.println("Payment Succeeded : source channel: " + charge.getSource().getChannel());
            System.out.println("Payment Succeeded : source object: " + charge.getSource().getObject());
            System.out.println("Payment Succeeded : source payment method: " + charge.getSource().getPaymentMethodStringValue());
            System.out.println("Payment Succeeded : source payment type: " + charge.getSource().getPaymentType());
            System.out.println("Payment Succeeded : source type: " + charge.getSource().getType());
        }

        System.out.println("##############################################################################");
        if (charge.getExpiry() != null) {
            System.out.println("Payment Succeeded : expiry type :" + charge.getExpiry().getType());
            System.out.println("Payment Succeeded : expiry period :" + charge.getExpiry().getPeriod());
        }

        //saveCustomerRefInSession(charge);
        //configureSDKSession();
        showDialog(charge.getId(), charge.getResponse().getMessage(), company.tap.gosellapi.R.drawable.ic_checkmark_normal);
    }

    @Override
    public void paymentFailed(@Nullable Charge charge) {
        System.out.println("Payment Failed : " + charge.getStatus());
        System.out.println("Payment Failed : " + charge.getDescription());
        System.out.println("Payment Failed : " + charge.getResponse().getMessage());
        makePay("F",charge.getId(),charge.getResponse().getMessage(),charge.getTransaction().getAuthorizationID());

        showDialog(charge.getId(), charge.getResponse().getMessage(), company.tap.gosellapi.R.drawable.icon_failed);
    }

    public void makePay(String payStat,String id,String respon,String tran_id){

        MethodClass.showProgressDialog(this);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = getString(R.string.SERVER_URL) + "make-payment";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("order_id",order_id);
        params.put("invoice_data",tran_id);
        params.put("response_data",respon);
        params.put("txn_id",id);
        params.put("payment_status",payStat);
        params.put("device_id",android_id);
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("parames", jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(PlaceOrderActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(PlaceOrderActivity.this, response);
                    if (resultResponse != null) {
                        if(resultResponse.getString("status").equals("success")){
                            JSONObject message = resultResponse.getJSONObject("message");
                            LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(PlaceOrderActivity.this, DialogTypes.TYPE_SUCCESS)
                                    .setTitle(message.getString("message"))
                                    .setDescription(message.getString("meaning"))
                                    .setPositiveText("Ok")
                                    .setPositiveListener(new ClickListener() {
                                        @Override
                                        public void onClick(LottieAlertDialog lottieAlertDialog) {
                                            lottieAlertDialog.dismiss();
                                            Intent intent = new Intent(PlaceOrderActivity.this, OrderDetailsActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.putExtra("order_id",order_id);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .build();
                            alertDialog.show();
                        }else {
                            JSONObject message = resultResponse.getJSONObject("message");
                            LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(PlaceOrderActivity.this, DialogTypes.TYPE_ERROR)
                                    .setTitle(message.getString("message"))
                                    .setDescription(message.getString("meaning"))
                                    .setPositiveText("Ok")
                                    .setPositiveListener(new ClickListener() {
                                        @Override
                                        public void onClick(LottieAlertDialog lottieAlertDialog) {
                                            lottieAlertDialog.dismiss();
                                        }
                                    })
                                    .build();
                            alertDialog.show();
                            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    onBackPressed();
                                }
                            });
                        }

                    }
                } catch (Exception e) {
                    MethodClass.error_alert(PlaceOrderActivity.this);
                    //scrollView.setVisibility(View.GONE);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());
                //scrollView.setVisibility(View.GONE);
                MethodClass.hideProgressDialog(PlaceOrderActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(PlaceOrderActivity.this);
                } else {
                    MethodClass.error_alert(PlaceOrderActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(PlaceOrderActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(PlaceOrderActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(PlaceOrderActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(PlaceOrderActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void authorizationSucceed(@NonNull Authorize authorize) {
        System.out.println("Authorize Succeeded : " + authorize.getStatus());
        System.out.println("Authorize Succeeded : " + authorize.getResponse().getMessage());

        if (authorize.getCard() != null) {
            System.out.println("Payment Authorized Succeeded : first six : " + authorize.getCard().getFirstSix());
            System.out.println("Payment Authorized Succeeded : last four: " + authorize.getCard().getLast4());
            System.out.println("Payment Authorized Succeeded : card object : " + authorize.getCard().getObject());
        }

        System.out.println("##############################################################################");
        if (authorize.getAcquirer() != null) {
            System.out.println("Payment Authorized Succeeded : acquirer id : " + authorize.getAcquirer().getId());
            System.out.println("Payment Authorized Succeeded : acquirer response code : " + authorize.getAcquirer().getResponse().getCode());
            System.out.println("Payment Authorized Succeeded : acquirer response message: " + authorize.getAcquirer().getResponse().getMessage());
        }
        System.out.println("##############################################################################");
        if (authorize.getSource() != null) {
            System.out.println("Payment Authorized Succeeded : source id: " + authorize.getSource().getId());
            System.out.println("Payment Authorized Succeeded : source channel: " + authorize.getSource().getChannel());
            System.out.println("Payment Authorized Succeeded : source object: " + authorize.getSource().getObject());
            System.out.println("Payment Authorized Succeeded : source payment method: " + authorize.getSource().getPaymentMethodStringValue());
            System.out.println("Payment Authorized Succeeded : source payment type: " + authorize.getSource().getPaymentType());
            System.out.println("Payment Authorized Succeeded : source type: " + authorize.getSource().getType());
        }

        System.out.println("##############################################################################");
        if (authorize.getExpiry() != null) {
            System.out.println("Payment Authorized Succeeded : expiry type :" + authorize.getExpiry().getType());
            System.out.println("Payment Authorized Succeeded : expiry period :" + authorize.getExpiry().getPeriod());
        }


        //saveCustomerRefInSession(authorize);
        configureSDKSession();
        showDialog(authorize.getId(), authorize.getResponse().getMessage(), company.tap.gosellapi.R.drawable.ic_checkmark_normal);
    }

    @Override
    public void authorizationFailed(Authorize authorize) {
        System.out.println("Authorize Failed : " + authorize.getStatus());
        System.out.println("Authorize Failed : " + authorize.getDescription());
        System.out.println("Authorize Failed : " + authorize.getResponse().getMessage());
        showDialog(authorize.getId(), authorize.getResponse().getMessage(), company.tap.gosellapi.R.drawable.icon_failed);
    }


    @Override
    public void cardSaved(@NonNull Charge charge) {
        // Cast charge object to SaveCard first to get all the Card info.
        if (charge instanceof SaveCard) {
            System.out.println("Card Saved Succeeded : first six digits : " + ((SaveCard) charge).getCard().getFirstSix() + "  last four :" + ((SaveCard) charge).getCard().getLast4());
        }
        System.out.println("Card Saved Succeeded : " + charge.getStatus());
        System.out.println("Card Saved Succeeded : " + charge.getCard().getBrand());
        System.out.println("Card Saved Succeeded : " + charge.getDescription());
        System.out.println("Card Saved Succeeded : " + charge.getResponse().getMessage());
        //saveCustomerRefInSession(charge);
        showDialog(charge.getId(), charge.getStatus().toString(), company.tap.gosellapi.R.drawable.ic_checkmark_normal);
    }

    @Override
    public void cardSavingFailed(@NonNull Charge charge) {
        System.out.println("Card Saved Failed : " + charge.getStatus());
        System.out.println("Card Saved Failed : " + charge.getDescription());
        System.out.println("Card Saved Failed : " + charge.getResponse().getMessage());
        showDialog(charge.getId(), charge.getStatus().toString(), company.tap.gosellapi.R.drawable.icon_failed);
    }

    @Override
    public void cardTokenizedSuccessfully(@NonNull Token token) {
        System.out.println("Card Tokenized Succeeded : ");
        System.out.println("Token card : " + token.getCard().getFirstSix() + " **** " + token.getCard().getLastFour());
        System.out.println("Token card : " + token.getCard().getFingerprint() + " **** " + token.getCard().getFunding());
        System.out.println("Token card : " + token.getCard().getId() + " ****** " + token.getCard().getName());
        System.out.println("Token card : " + token.getCard().getAddress() + " ****** " + token.getCard().getObject());
        System.out.println("Token card : " + token.getCard().getExpirationMonth() + " ****** " + token.getCard().getExpirationYear());

        showDialog(token.getId(), "Token", company.tap.gosellapi.R.drawable.ic_checkmark_normal);
    }

    @Override
    public void savedCardsList(@NonNull CardsList cardsList) {
        if (cardsList != null && cardsList.getCards() != null) {
            System.out.println(" Card List Response Code : " + cardsList.getResponseCode());
            System.out.println(" Card List Top 10 : " + cardsList.getCards().size());
            System.out.println(" Card List Has More : " + cardsList.isHas_more());
            System.out.println("----------------------------------------------");
            for (SavedCard sc : cardsList.getCards()) {
                System.out.println(sc.getBrandName());
            }
            System.out.println("----------------------------------------------");

            //showSavedCardsDialog(cardsList);
        }
    }


    @Override
    public void sdkError(@Nullable GoSellError goSellError) {
        if (progress != null)
            progress.dismiss();
        if (goSellError != null) {
            Log.e("sdkError: ","SDK Process Error : " + goSellError.getErrorBody());
            Log.e("sdkError: ","SDK Process Error : " + goSellError.getErrorMessage());
            Log.e("sdkError: ","SDK Process Error : " + goSellError.getErrorCode());
            showDialog(goSellError.getErrorCode() + "", goSellError.getErrorMessage(), company.tap.gosellapi.R.drawable.icon_failed);
        }
    }


    @Override
    public void sessionIsStarting() {
        System.out.println(" Session Is Starting.....");
    }

    @Override
    public void sessionHasStarted() {
        System.out.println(" Session Has Started .......");
    }


    @Override
    public void sessionCancelled() {
        Log.d("MainActivity", "Session Cancelled.........");
    }



    @Override
    public void sessionFailedToStart() {
        Log.d("MainActivity", "Session Failed to start.........");
    }


    @Override
    public void invalidCardDetails() {
        System.out.println(" Card details are invalid....");
    }

    @Override
    public void backendUnknownError(String message) {
        System.out.println("Backend Un-Known error.... : " + message);
    }

    @Override
    public void invalidTransactionMode() {
        System.out.println(" invalidTransactionMode  ......");
    }

    @Override
    public void invalidCustomerID() {
        if (progress != null)
            progress.dismiss();
        System.out.println("Invalid Customer ID .......");

    }

    @Override
    public void userEnabledSaveCardOption(boolean saveCardEnabled) {
        System.out.println("userEnabledSaveCardOption :  " + saveCardEnabled);
    }


/////////////////////////////////////////////////////////  needed only for demo ////////////////////


//    public void showSavedCardsDialog(CardsList cardsList) {
//        if (progress != null)
//            progress.dismiss();
//
//        if (cardsList != null && cardsList.getCards() != null && cardsList.getCards().size() == 0) {
//            Toast.makeText(this, "There is no card saved for this customer", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
////        recyclerView.setHasFixedSize(true);
//
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//
//        data = new ArrayList<SavedCard>();
//
//        removedItems = new ArrayList<Integer>();
//
//        adapter = new CustomAdapter(cardsList.getCards());
//        recyclerView.setAdapter(adapter);
//
//
//    }

    private void showDialog(String chargeID, String msg, int icon) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        PopupWindow popupWindow;
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {

                View layout = inflater.inflate(company.tap.gosellapi.R.layout.charge_status_layout, findViewById(
                        company.tap.gosellapi.R.id.popup_element));

                popupWindow = new PopupWindow(layout, width, 250, true);

                ImageView status_icon = layout.findViewById(company.tap.gosellapi.R.id.status_icon);
                TextView statusText = layout.findViewById(company.tap.gosellapi.R.id.status_text);
                TextView chargeText = layout.findViewById(company.tap.gosellapi.R.id.charge_id_txt);
                status_icon.setImageResource(icon);
//                status_icon.setVisibility(View.INVISIBLE);
                chargeText.setText(chargeID);
                statusText.setText((msg != null && msg.length() > 30) ? msg.substring(0, 29) : msg);


                popupWindow.showAtLocation(layout, Gravity.TOP, 0, 50);
                popupWindow.getContentView().startAnimation(AnimationUtils.loadAnimation(this, R.anim.popup_show));

                setupTimer(popupWindow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTimer(PopupWindow popupWindow) {
        // Hide after some seconds
        final Handler handler = new Handler();
        final Runnable runnable = () -> {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        };

        popupWindow.setOnDismissListener(() -> handler.removeCallbacks(runnable));

        handler.postDelayed(runnable, 4000);
    }

    private Customer getCustomer() {
        String name=PreferenceManager.getDefaultSharedPreferences(this).getString("email","");
        Log.e("Customer Name: ",name);
        String email=PreferenceManager.getDefaultSharedPreferences(this).getString("email","");
        Log.e("Customer email: ",email);
        String country_phone_code=PreferenceManager.getDefaultSharedPreferences(this).getString("country_phone_code","");
        Log.e("Customer phone_code: ",country_phone_code);
        String phone_number=PreferenceManager.getDefaultSharedPreferences(this).getString("phone_number","");
        Log.e("Customer phone_number: ",phone_number);
        return new Customer.CustomerBuilder(null).email(email).firstName(name).metadata("").phone(new PhoneNumber(country_phone_code,phone_number)).build();
    }

    //end of payment

    public void back(View view) {
        onBackPressed();
    }

    public void getOrderData() {
        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        MethodClass.showProgressDialog(this);

        String server_url = getString(R.string.SERVER_URL) + "get-order-data/"+getIntent().getStringExtra("order_no");
        Log.e("server_url", server_url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, server_url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(PlaceOrderActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(PlaceOrderActivity.this, response);
                    if (resultResponse != null) {
                        JSONObject order = resultResponse.getJSONObject("order");
                        String conv_factor = order.getString("conv_factor" );
                        JSONObject currency = order.getJSONObject("currency");
                        String currency_code = currency.getString("currency_code");

                        order_id=order.getString("id");
                        if (Double.parseDouble(order.getString("shipping_price")) != 0){
                            shipping_price_tv.setText((order.getString("shipping_price") +" "+currency_code));
                            shipping_price_lay.setVisibility(View.VISIBLE);
                        }else {
                            shipping_price_lay.setVisibility(View.GONE);
                        }
                        item_tv.setText(resultResponse.getString("total_item") + getString(R.string.items));
                        total_price_tv.setText(order.getString("subtotal") +" "+currency_code);
                        if (Float.parseFloat(order.getString("total_discount")) > 0) {
                            discount_tv.setText((order.getString("total_discount") +" "+currency_code));
                            discount_lay.setVisibility(View.VISIBLE);
                        } else {
                            discount_lay.setVisibility(View.GONE);
                        }
                        paybale_amount_tv.setText((order.getString("payable_amount") +" "+currency_code));
                        order_total = (order.getString("payable_amount"));

                        String adjustment=order.getString("adjustment");
                        if (Double.parseDouble(adjustment)!=0){
                            wallet_tv.setText(adjustment+" "+currency_code);
                            wallet_lay.setVisibility(View.VISIBLE);
                        }else {
                            wallet_lay.setVisibility(View.GONE);
                        }

                        String payment_method=order.getString("payment_method");
                        if (payment_method.equals("C")){
                            String cod_cha=resultResponse.getString("cod_charge");
                            cod_charge_tv.setText(noFormat(Double.parseDouble(cod_cha))+" "+currency_code);
                            cod_btn.setVisibility(View.VISIBLE);
                            cod_charge_lay.setVisibility(View.VISIBLE);
                            online_btn.setVisibility(View.GONE);
                            payment_btn.setText(getString(R.string.place_order));
                            payButtonView.setVisibility(View.GONE);
                        }else if (payment_method.equals("O")){
                            cod_btn.setVisibility(View.GONE);
                            cod_charge_lay.setVisibility(View.GONE);
                            online_btn.setVisibility(View.VISIBLE);
                            payment_btn.setText(getString(R.string.continue_to_pay));
                            payment_btn.setVisibility(View.GONE);
                            startSDK();
                            payButtonView.setVisibility(View.VISIBLE);
                        }else if (payment_method.equals("W")){
                            cod_btn.setVisibility(View.GONE);
                            cod_charge_lay.setVisibility(View.GONE);
                            online_btn.setVisibility(View.GONE);
                            payment_btn.setText(getString(R.string.place_order));
                            payButtonView.setVisibility(View.GONE);
                            payment_method_lay.setVisibility(View.GONE);
                        }



                        name.setText(order.getString("shipping_full_name"));
                        phone.setText(order.getString("shipping_phone"));
                        String full_address="";
                        full_address=order.getString("shipping_address_line_1");
                        full_address=full_address+", "+MethodClass.checkNull(order.getString("shipping_address_line_2"));
                        full_address=full_address+", "+MethodClass.checkNull(order.getString("shipping_city"));
                        full_address=full_address+", "+MethodClass.checkNull(order.getString("shipping_state"));
                        full_address=full_address+", "+MethodClass.checkNull(resultResponse.getString("shipping_country_name"));
                        full_address=full_address+", "+MethodClass.checkNull(order.getString("shipping_pincode"));
                        address.setText(full_address);

                        name_1.setText(order.getString("billing_full_name"));
                        phone_1.setText(order.getString("billing_phone"));
                        String full_address_1="";
                        full_address_1=order.getString("billing_address_line_1");
                        full_address_1=full_address_1+", "+MethodClass.checkNull(order.getString("billing_address_line_2"));
                        full_address_1=full_address_1+", "+MethodClass.checkNull(order.getString("billing_city"));
                        full_address_1=full_address_1+", "+MethodClass.checkNull(order.getString("billing_state"));
                        full_address_1=full_address_1+", "+MethodClass.checkNull(resultResponse.getString("billing_country_name"));
                        full_address_1=full_address_1+", "+MethodClass.checkNull(order.getString("billing_pincode"));
                        address_1.setText(full_address_1);

                        JSONArray order_master_details = order.getJSONArray("order_master_details");
                        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                        for (int i = 0; i < order_master_details.length(); i++) {
                            JSONObject cart_obj = order_master_details.getJSONObject(i);
                            String cart_details_id = cart_obj.getString("id");
                            String order_master_id = cart_obj.getString("order_master_id");
                            String total = cart_obj.getString("total");
                            String quantity = cart_obj.getString("quantity");
                            //String power_type_no_power = cart_obj.getString("power_type_no_power");
                            //String power_type_with_power = cart_obj.getString("power_type_with_power");
                            String power = cart_obj.getString("power");
                            String left_eye = MethodClass.checkNull(cart_obj.getString("left_eye"));
                            String right_eye = MethodClass.checkNull(cart_obj.getString("right_eye"));
                            String left_sph = MethodClass.checkNull(cart_obj.getString("left_sph"));
                            String left_cyl = MethodClass.checkNull(cart_obj.getString("left_cyl"));
                            String left_axis = MethodClass.checkNull(cart_obj.getString("left_axis"));
                            String left_base_curve = MethodClass.checkNull(cart_obj.getString("left_base_curve"));
                            String right_sph = MethodClass.checkNull(cart_obj.getString("right_sph"));
                            String right_cyl = MethodClass.checkNull(cart_obj.getString("right_cyl"));
                            String right_axis = MethodClass.checkNull(cart_obj.getString("right_axis"));
                            String right_base_curve = MethodClass.checkNull(cart_obj.getString("right_base_curve"));


                            JSONObject product_by_language = cart_obj.getJSONObject("product_by_language");
                            String product_id = product_by_language.getString("product_id");
                            String title = product_by_language.getString("title");
                            String description = product_by_language.getString("description");

                            JSONObject default_image = cart_obj.getJSONObject("default_image");
                            String image = default_image.getString("image");

                            JSONObject category = cart_obj.getJSONObject("product_details").getJSONObject("product_parent_category").getJSONObject("category");
                            String pro_cat = category.getJSONObject("category_by_language").getString("title");

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put(CART_DETAILS_ID, cart_details_id);
                            hashMap.put(CART_MASTER_ID, order_master_id);
                            hashMap.put(PRODUCT_PRICE, total);
                            hashMap.put(PRODUCT_QTY, quantity);
                            hashMap.put(PRODUCT_ID, product_id);
                            hashMap.put(PRODUCT_TITLE, title);
                            hashMap.put(PRODUCT_DESC, description);
                            hashMap.put(PRODUCT_IMAGE, image);
                            hashMap.put(POWER, power);
                            hashMap.put(LEFT_EYE, left_eye);
                            hashMap.put(RIGHT_EYE, right_eye);
                            hashMap.put(LEFT_SPH, left_sph);
                            hashMap.put(LEFT_CYL, left_cyl);
                            hashMap.put(LEFT_AXIS, left_axis);
                            hashMap.put(LEFT_B_C, left_base_curve);
                            hashMap.put(RIGHT_SPH, right_sph);
                            hashMap.put(RIGHT_CYL, right_cyl);
                            hashMap.put(RIGHT_AXIS, right_axis);
                            hashMap.put(RIGHT_B_C, right_base_curve);
                            hashMap.put(CATEGORY, pro_cat);
                            hashMap.put(CURR_CODE, currency_code);
                            arrayList.add(hashMap);
                        }
                        OrderPlaceAdapter adapter = new OrderPlaceAdapter(PlaceOrderActivity.this, arrayList);
                        recy_view_trend_prod.setAdapter(adapter);

                        scrollView.setVisibility(View.VISIBLE);

                    }
                } catch (JSONException e) {
                    FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
                    MethodClass.error_alert(PlaceOrderActivity.this);
                    scrollView.setVisibility(View.GONE);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
                // Log.e("error2", error.getMessage());
                scrollView.setVisibility(View.GONE);
                MethodClass.hideProgressDialog(PlaceOrderActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(PlaceOrderActivity.this);
                } else {
                    MethodClass.error_alert(PlaceOrderActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(PlaceOrderActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(PlaceOrderActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(PlaceOrderActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(PlaceOrderActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    public void placeOrder() {
        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        MethodClass.showProgressDialog(this);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = getString(R.string.SERVER_URL) + "place-order";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("order_id", order_id);
        params.put("device_type", "A");
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("parames", jsonObject.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(PlaceOrderActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(PlaceOrderActivity.this, response);
                    if (resultResponse != null) {
                        JSONObject MainMsg = resultResponse.getJSONObject("message");
                        String message = MainMsg.getString("message");
                        String meaning = MainMsg.getString("meaning");
                        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(PlaceOrderActivity.this, DialogTypes.TYPE_SUCCESS)
                                .setTitle(message)
                                .setDescription(meaning)
                                .setPositiveText(getString(R.string.ok))
                                .setPositiveListener(new ClickListener() {
                                    @Override
                                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                                        lottieAlertDialog.dismiss();
                                    }
                                })
                                .build();
                        alertDialog.show();
                        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                Intent intent = new Intent(PlaceOrderActivity.this, OrderDetailsActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("order_id",order_id);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
                    MethodClass.error_alert(PlaceOrderActivity.this);
                    scrollView.setVisibility(View.GONE);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
                 Log.e("error2", error.getMessage());
                scrollView.setVisibility(View.GONE);
                MethodClass.hideProgressDialog(PlaceOrderActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(PlaceOrderActivity.this);
                } else {
                    MethodClass.error_alert(PlaceOrderActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(PlaceOrderActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(PlaceOrderActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(PlaceOrderActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(PlaceOrderActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}