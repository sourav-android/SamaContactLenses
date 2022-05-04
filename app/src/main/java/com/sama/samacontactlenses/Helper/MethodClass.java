package com.sama.samacontactlenses.Helper;

import static com.sama.samacontactlenses.Common.Constant.NOTIFICATION_COUNT;
import static com.sama.samacontactlenses.Common.Constant.USER_IMAGE_URL;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.sama.samacontactlenses.Activity.AboutUsActivity;
import com.sama.samacontactlenses.Activity.AllCategoryActivity;
import com.sama.samacontactlenses.Activity.ContactUsActivity;
import com.sama.samacontactlenses.Activity.FAQActivity;
import com.sama.samacontactlenses.Activity.HomeActivity;
import com.sama.samacontactlenses.Activity.LanguageActivity;
import com.sama.samacontactlenses.Activity.LoginActivity;
import com.sama.samacontactlenses.Activity.MyFavoriteActivity;
import com.sama.samacontactlenses.Activity.NotificationActivity;
import com.sama.samacontactlenses.Activity.OrderHistoryActivity;
import com.sama.samacontactlenses.Activity.OrderTrack2Activity;
import com.sama.samacontactlenses.Activity.UserActivity;
import com.sama.samacontactlenses.Adapter.CountryCodeRecyAdapter;
import com.sama.samacontactlenses.Adapter.CountryNameRecy2Adapter;
import com.sama.samacontactlenses.Adapter.CountryNameRecyAdapter;
import com.sama.samacontactlenses.Adapter.CountryNameRecyAdapter3;
import com.sama.samacontactlenses.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class MethodClass {


    /*public static void setBottomFun(final Activity activity) {
        final DrawerLayout drawer_layout = activity.findViewById(R.id.drawer_layout);
        ImageView menu_img = activity.findViewById(R.id.menu_img);
        ImageView logout_img = activity.findViewById(R.id.logout_img);
        ImageView home_img = activity.findViewById(R.id.home_img);
        ImageView collection_img = activity.findViewById(R.id.collection_img);

        menu_img.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {
                drawer_layout.openDrawer(Gravity.START);
            }
        });


        logout_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("is_logged_in",false)){
                    *//*LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_WARNING)
                            .setTitle("Log Out")
                            .setDescription("Do you want to log out?")
                            .setPositiveText("Okay")
                            .setNegativeText("Cancel")
                            .setPositiveListener(new ClickListener() {
                                @Override
                                public void onClick(LottieAlertDialog lottieAlertDialog) {
                                    lottieAlertDialog.dismiss();
                                    Userlogout(activity);
                                }
                            }).setNegativeListener(new ClickListener() {
                                @Override
                                public void onClick(LottieAlertDialog lottieAlertDialog) {
                                    lottieAlertDialog.dismiss();
                                }
                            })
                            .build();
                    alertDialog.show();*//*
                    Toast.makeText(activity, "Working Progress", Toast.LENGTH_SHORT).show();
                }else {
                    go_to_next_activity(activity,LoginActivity.class);
                }


            }

        });

        home_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!activity.getLocalClassName().equals("Activity.HomeActivity")) {
                    Intent intent = new Intent(activity, HomeActivity.class);
                    activity.startActivity(intent);
                }
            }
        });
        collection_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!activity.getLocalClassName().equals("Activity.SearchActivity")) {
                    Intent intent = new Intent(activity, SearchActivity.class);
                    activity.startActivity(intent);
                }
            }
        });


    }*/

    public static void otpWaitFun(Activity activity,String show_hide){
        LinearLayout otp_wait = activity.findViewById(R.id.otp_wait);
        if (show_hide.equals("S")){
            otp_wait.setVisibility(View.VISIBLE);
             new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                otp_wait.setVisibility(View.GONE);
            }
        },60000);
        }else {
            otp_wait.setVisibility(View.GONE);
        }

    }


    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    public static void share(Activity activity, String title, String product_id, String imgurl) {
        String get_title = title;

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.samacontactslens.app/" + product_id))
                .setDomainUriPrefix("https://samacontactlenses.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.lensapp.samacontact").build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(get_title)
                                /*.setDescription("This link works whether the app is installed or not!")*/
                                .setImageUrl(Uri.parse(imgurl))
                                .build())
                .buildDynamicLink();

        Uri uri = dynamicLink.getUri();

        /*Create an ACTION_SEND Intent*/
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        /*This will be the actual content you wish you share.*/
        String shareBody = get_title;
        /*The type of the content is text, obviously.*/
        intent.setType("text/plain");
        /*Applying information Subject and Body.*/
        //intent.putExtra(android.content.Intent.EXTRA_SUBJECT, String.valueOf(uri));
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody + "\n" + String.valueOf(uri));
        /*Fire!*/
        activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.share_using)));
    }



    public static void setBottomFun2(Activity activity, MeowBottomNavigation btmNav) {
        if (activity.getLocalClassName().equals("Activity.HomeActivity")) {
            btmNav.show(2, true);
        } else if (activity.getLocalClassName().equals("Activity.AllCategoryActivity")) {
            btmNav.show(3, true);
        } else if (activity.getLocalClassName().equals("Activity.UserActivity")) {
            btmNav.show(4, true);
        }
    }

    public static void setBottomFun(Activity activity) {
        final DrawerLayout drawer_layout = activity.findViewById(R.id.drawer_layout);
        MeowBottomNavigation bottomNavigation = activity.findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_list));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_shopping_bag));
        bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_user_94));

        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                // your codes
                if (item.getId() == 1) {
                    drawer_layout.openDrawer(Gravity.START);
                }
                if (item.getId() == 2) {
                    if (!activity.getLocalClassName().equals("Activity.HomeActivity")) {
                        Intent intent = new Intent(activity, HomeActivity.class);
                        activity.startActivity(intent);
                    }
                }
                if (item.getId() == 3) {
                    if (!activity.getLocalClassName().equals("Activity.AllCategoryActivity")) {
                        Intent intent = new Intent(activity, AllCategoryActivity.class);
                        activity.startActivity(intent);
                    }
                }
                if (item.getId() == 4) {
                    if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("is_logged_in", false)) {
                        if (!activity.getLocalClassName().equals("Activity.UserActivity")) {
                            Intent intent = new Intent(activity, UserActivity.class);
                            activity.startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
                    }
                }
            }
        });

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                // your codes
                /*if (item.getId() == 1) {
                    drawer_layout.openDrawer(Gravity.START);
                }
                if (item.getId() == 2) {
                    if (!activity.getLocalClassName().equals("Activity.HomeActivity")) {
                        Intent intent = new Intent(activity, HomeActivity.class);
                        activity.startActivity(intent);
                    }
                }
                if (item.getId() == 3) {
                    if (!activity.getLocalClassName().equals("Activity.AllCategoryActivity")) {
                        Intent intent = new Intent(activity, AllCategoryActivity.class);
                        activity.startActivity(intent);
                    }
                }
                if (item.getId() == 4) {
                    if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("is_logged_in", false)) {
                    *//*LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_WARNING)
                            .setTitle("Log Out")
                            .setDescription("Do you want to log out?")
                            .setPositiveText("Okay")
                            .setNegativeText("Cancel")
                            .setPositiveListener(new ClickListener() {
                                @Override
                                public void onClick(LottieAlertDialog lottieAlertDialog) {
                                    lottieAlertDialog.dismiss();
                                    Userlogout(activity);
                                }
                            }).setNegativeListener(new ClickListener() {
                                @Override
                                public void onClick(LottieAlertDialog lottieAlertDialog) {
                                    lottieAlertDialog.dismiss();
                                }
                            })
                            .build();
                    alertDialog.show();*//*
                    } else {
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
                    }
                }*/
            }
        });

        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                if (item.getId() == 1) {
                    drawer_layout.openDrawer(Gravity.START);
                }
                if (item.getId() == 2) {
                    if (!activity.getLocalClassName().equals("Activity.HomeActivity")) {
                        Intent intent = new Intent(activity, HomeActivity.class);
                        activity.startActivity(intent);
                    }
                }
                if (item.getId() == 3) {
                    if (!activity.getLocalClassName().equals("Activity.AllCategoryActivity")) {
                        Intent intent = new Intent(activity, AllCategoryActivity.class);
                        activity.startActivity(intent);
                    }
                }
                if (item.getId() == 4) {
                    if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("is_logged_in", false)) {
                        if (!activity.getLocalClassName().equals("Activity.UserActivity")) {
                            Intent intent = new Intent(activity, UserActivity.class);
                            activity.startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
                    }
                }
            }
        });

        if (activity.getLocalClassName().equals("Activity.HomeActivity")) {
            bottomNavigation.show(2, true);
        } else if (activity.getLocalClassName().equals("Activity.SearchActivity")) {
            bottomNavigation.show(3, true);
        }
    }

    public static void setMenu(final Activity activity) {
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        final DrawerLayout drawer_layout = activity.findViewById(R.id.drawer_layout);
        if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("is_logged_in", false)) {
            CircleImageView image_nav = headerView.findViewById(R.id.image_nav);
            TextView name_nav = headerView.findViewById(R.id.name_nav);
            TextView email_nav = headerView.findViewById(R.id.email_nav);
            String user_image = USER_IMAGE_URL + PreferenceManager.getDefaultSharedPreferences(activity).getString("profile_pic", "");
            String user_name = PreferenceManager.getDefaultSharedPreferences(activity).getString("name", "");
            String user_email = PreferenceManager.getDefaultSharedPreferences(activity).getString("email", "");
            Picasso.get().load(user_image).placeholder(R.drawable.logo_2).error(R.drawable.logo_2).into(image_nav);
            name_nav.setText(user_name);
            if (!user_email.equals("") && !user_email.equals(null) && !user_email.equals("null")) {
                email_nav.setText(user_email);
            }
        }
        TextView notification_count_tv = headerView.findViewById(R.id.notification_count_tv);
        if (!checkNull(NOTIFICATION_COUNT).equals("") && !NOTIFICATION_COUNT.equals("0")) {
            notification_count_tv.setText(NOTIFICATION_COUNT);
            notification_count_tv.setVisibility(View.VISIBLE);
        } else {
            notification_count_tv.setVisibility(View.GONE);
        }

        LinearLayout home_nav_lin = headerView.findViewById(R.id.home_nav_lin);
        LinearLayout collection_nav_lin = headerView.findViewById(R.id.collection_nav_lin);
        LinearLayout order_his_nav_lin = headerView.findViewById(R.id.order_his_nav_lin);
        LinearLayout my_favorite_nav_lin = headerView.findViewById(R.id.my_favorite_nav_lin);
        LinearLayout track_order_nav_lin = headerView.findViewById(R.id.track_order_nav_lin);
        LinearLayout notification_nav_lin = headerView.findViewById(R.id.notification_nav_lin);
        LinearLayout about_nav_lin = headerView.findViewById(R.id.about_nav_lin);
        LinearLayout contact_nav_lin = headerView.findViewById(R.id.contact_nav_lin);
        LinearLayout faq_nav_lin = headerView.findViewById(R.id.faq_nav_lin);
        LinearLayout logout_login_nav_lin = headerView.findViewById(R.id.logout_login_nav_lin);
        LinearLayout lang_nav_lin = headerView.findViewById(R.id.lang_nav_lin);
        TextView login_logout_tv = headerView.findViewById(R.id.login_logout_tv);

        if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("is_logged_in", false)) {
            login_logout_tv.setText(activity.getString(R.string.logout));
            order_his_nav_lin.setVisibility(View.VISIBLE);
            my_favorite_nav_lin.setVisibility(View.VISIBLE);
        } else {
            login_logout_tv.setText(activity.getString(R.string.login));
            order_his_nav_lin.setVisibility(View.GONE);
            my_favorite_nav_lin.setVisibility(View.GONE);
        }

        home_nav_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.closeDrawers();
                if (!activity.getLocalClassName().equals("Activity.HomeActivity")) {
                    Intent intent = new Intent(activity, HomeActivity.class);
                    activity.startActivity(intent);
                }
            }
        });

        collection_nav_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.closeDrawers();
                if (!activity.getLocalClassName().equals("Activity.AllCategoryActivity")) {
                    Intent intent = new Intent(activity, AllCategoryActivity.class);
                    activity.startActivity(intent);
                }
            }
        });
        lang_nav_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.closeDrawers();
                if (!activity.getLocalClassName().equals("Activity.LanguageActivity")) {
                    Intent intent = new Intent(activity, LanguageActivity.class);
                    activity.startActivity(intent);
                }
            }
        });

        order_his_nav_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.closeDrawers();
                if (!activity.getLocalClassName().equals("Activity.OrderHistoryActivity")) {
                    Intent intent = new Intent(activity, OrderHistoryActivity.class);
                    activity.startActivity(intent);
                }

            }
        });
        my_favorite_nav_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.closeDrawers();
                if (!activity.getLocalClassName().equals("Activity.MyFavoriteActivity")) {
                    Intent intent = new Intent(activity, MyFavoriteActivity.class);
                    activity.startActivity(intent);
                }

            }
        });
        track_order_nav_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.closeDrawers();
                if (!activity.getLocalClassName().equals("Activity.OrderTrack2Activity")) {
                    Intent intent = new Intent(activity, OrderTrack2Activity.class);
                    activity.startActivity(intent);
                }

            }
        });
        notification_nav_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.closeDrawers();
                if (!activity.getLocalClassName().equals("Activity.NotificationActivity")) {
                    Intent intent = new Intent(activity, NotificationActivity.class);
                    activity.startActivity(intent);
                }

            }
        });

        about_nav_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.closeDrawers();
                if (!activity.getLocalClassName().equals("Activity.AboutUsActivity")) {
                    Intent intent = new Intent(activity, AboutUsActivity.class);
                    activity.startActivity(intent);
                }
            }
        });
        contact_nav_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.closeDrawers();
                if (!activity.getLocalClassName().equals("Activity.ContactUsActivity")) {
                    Intent intent = new Intent(activity, ContactUsActivity.class);
                    activity.startActivity(intent);
                }
            }
        });
        faq_nav_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.closeDrawers();
                if (!activity.getLocalClassName().equals("Activity.FAQActivity")) {
                    Intent intent = new Intent(activity, FAQActivity.class);
                    activity.startActivity(intent);
                }
            }
        });

        logout_login_nav_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.closeDrawers();
                if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("is_logged_in", false)) {
                    LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_WARNING)
                            .setTitle(activity.getString(R.string.log_out))
                            .setDescription(activity.getString(R.string.do_you_want_to_log_out))
                            .setPositiveText(activity.getString(R.string.okay))
                            .setNegativeText(activity.getString(R.string.cancel))
                            .setPositiveListener(new ClickListener() {
                                @Override
                                public void onClick(LottieAlertDialog lottieAlertDialog) {
                                    lottieAlertDialog.dismiss();
                                    Userlogout(activity);
                                }
                            }).setNegativeListener(new ClickListener() {
                                @Override
                                public void onClick(LottieAlertDialog lottieAlertDialog) {
                                    lottieAlertDialog.dismiss();
                                }
                            })
                            .build();
                    alertDialog.show();
                } else {
                    go_to_next_activity(activity, LoginActivity.class);
                }
            }
        });

       /* //////////////////////////CURRENCY WORK///////////////////////////////////////
        TextView currency_text = headerView.findViewById(R.id.currency_text);
        String setCurrency = PreferenceManager.getDefaultSharedPreferences(activity).getString("currency_code", "USD");
        if (!checkNull(setCurrency).equals("")) {
            currency_text.setText(activity.getString(R.string.curr) + " : " + checkNull(setCurrency));
        }
        curr_nav_lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.closeDrawers();
                get_currency_popup(currency_text, activity);
            }
        });
*/
        /*------Social Medial Work------*/
        ImageView whatsapp_img = headerView.findViewById(R.id.whatsapp_img);
        ImageView facebook_img = headerView.findViewById(R.id.facebook_img);
        ImageView twiiter_img = headerView.findViewById(R.id.twiiter_img);
        ImageView instagram_img = headerView.findViewById(R.id.instagram_img);
        ImageView youtube_img = headerView.findViewById(R.id.youtube_img);

        String facebook_url = PreferenceManager.getDefaultSharedPreferences(activity).getString("facebook_url", "");
        String twitter_url = PreferenceManager.getDefaultSharedPreferences(activity).getString("twitter_url", "");
        String instagram_url = PreferenceManager.getDefaultSharedPreferences(activity).getString("instagram_url", "");
        String whatsapp_no = PreferenceManager.getDefaultSharedPreferences(activity).getString("whatsapp_no", "");
        String youtube_url = PreferenceManager.getDefaultSharedPreferences(activity).getString("youtube_url", "");
        facebook_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(facebook_url));
                activity.startActivity(i);
            }
        });
        youtube_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(youtube_url));
                activity.startActivity(i);
            }
        });

        twiiter_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(twitter_url));
                activity.startActivity(i);
            }
        });
        instagram_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(instagram_url));
                activity.startActivity(i);
            }
        });
        whatsapp_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsAppConversation(activity, whatsapp_no, "hii..");
            }
        });
    }

    public static void openWhatsAppConversation(Context context, String number, String message) {

        number = number.replace(" ", "").replace("+", "");

        Intent sendIntent = new Intent("android.intent.action.MAIN");

        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
        sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(number) + "@s.whatsapp.net");

        context.startActivity(sendIntent);
    }

    public static void set_locale(Activity activity) {
       /* Resources res = activity.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(check_locale_lang(activity).toLowerCase())); // API 17+ only.
        } else {
            conf.locale = new Locale(check_locale_lang(activity));
        }
        res.updateConfiguration(conf, dm);*/

    }

    public static String check_locale_lang(Context activity) {
        String languageToLoad = PreferenceManager.getDefaultSharedPreferences(activity).getString("lang", "en");
        return languageToLoad;
    }
    public static Context updateBaseContextLocale(Context context) {
         String language = MethodClass.check_locale_lang(context); // Helper method to get saved language from SharedPreferences
        Locale locale = new Locale(language,"US");
        Locale.setDefault(locale);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            return updateResourcesLocale(context, locale);
        }

        return updateResourcesLocaleLegacy(context, locale);
    }

    @TargetApi(Build.VERSION_CODES.N_MR1)
    private static Context updateResourcesLocale(Context context, Locale locale) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }


    public static void hide_keyboard(Context context) {
        ((AppCompatActivity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public static void go_to_next_activity(Activity activity, Class next_activity) {
        activity.startActivity(new Intent(activity, next_activity));
    }


    static Dialog mDialog;

    public static void showProgressDialog(Activity activity) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        mDialog = new Dialog(activity);
        mDialog.setCancelable(false);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.custom_progress_dialog);
        mDialog.show();
    }

    public static void hideProgressDialog(Activity activity) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
   /* static LottieAlertDialog mDialog;

    public static void showProgressDialog(Activity activity) {
        mDialog = new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_LOADING)
                .setTitle("Loading")
                .setDescription("Please Wait")
                .build();
        mDialog.setCancelable(false);
        mDialog.show();
    }

    public static void hideProgressDialog(Activity activity) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }*/

    public static boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public static JSONObject Json_rpc_format(HashMap<String, String> params) {
        HashMap<String, Object> main_param = new HashMap<String, Object>();
        main_param.put("params", new JSONObject(params));
        main_param.put("jsonrpc", "2.0");
        return new JSONObject(main_param);
    }

    public static JSONObject Json_rpc_format_obj(HashMap<String, Object> params) {
        HashMap<String, Object> main_param = new HashMap<String, Object>();
        main_param.put("params", new JSONObject(params));
        main_param.put("jsonrpc", "2.0");
        return new JSONObject(main_param);
    }

    public static boolean isNetworkConnected(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static void customisNetworkConnected(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected())){
            network_error_alert(activity);
        }
    }



    public static JSONObject get_result_from_webservice(Activity activity, JSONObject response) {
        JSONObject result = null;
        if (response.has("error")) {
            try {
                String error = response.getString("error");
                JSONObject jsonObject = new JSONObject(error);
                if (jsonObject.getString("code").equals("-33085")) {
                    Toast.makeText(activity, jsonObject.getString("meaning"), Toast.LENGTH_SHORT).show();
                    Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(activity).getAll();
                    for (Map.Entry<String, ?> prefToReset : prefs.entrySet()) {
                        if (!prefToReset.getKey().equals("lang") && !prefToReset.getKey().equals("currency") && !prefToReset.getKey().equals("currency_code") && !prefToReset.getKey().equals("conv_factor") && !prefToReset.getKey().equals("country_id") && !prefToReset.getKey().equals("country_code") && !prefToReset.getKey().equals("is_first_time")) {
                            PreferenceManager.getDefaultSharedPreferences(activity).edit().remove(prefToReset.getKey()).commit();
                        }
                    }

                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);


                } else {
                    if (jsonObject.has("message")) {

                        if (jsonObject.has("meaning")) {
                            LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_ERROR)
                                    .setTitle(jsonObject.getString("message"))
                                    .setDescription(jsonObject.getString("meaning"))
                                    .setPositiveText(activity.getString(R.string.ok))
                                    .setPositiveListener(new ClickListener() {
                                        @Override
                                        public void onClick(LottieAlertDialog lottieAlertDialog) {
                                            lottieAlertDialog.dismiss();
                                        }
                                    })
                                    .build();
                            alertDialog.show();
                        } else {
                            LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_ERROR)
                                    .setTitle(jsonObject.getString("message"))
                                    .setPositiveText(activity.getString(R.string.ok))
                                    .setPositiveListener(new ClickListener() {
                                        @Override
                                        public void onClick(LottieAlertDialog lottieAlertDialog) {
                                            lottieAlertDialog.dismiss();
                                        }
                                    })
                                    .build();
                            alertDialog.show();
                        }

                    } else {
                        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_ERROR)
                                .setTitle(jsonObject.getString("message"))
                                .setPositiveText(activity.getString(R.string.ok))
                                .setPositiveListener(new ClickListener() {
                                    @Override
                                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                                        lottieAlertDialog.dismiss();
                                    }
                                })
                                .build();
                        alertDialog.show();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (response.has("result")) {
            try {
                result = response.getJSONObject("result");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("methodeJSONException", e.toString());
            }

        } else {
            Log.e("methodeElse", "methode Else");
            error_alert(activity);
        }
        return result;
    }

    public static void error_alert(Activity activity) {
        try {
            if (!activity.isFinishing()) {
                LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_ERROR)
                        .setTitle(activity.getString(R.string.oops))
                        .setDescription(activity.getString(R.string.something_went_wrong_please_try_again_later))
                        .setPositiveText(activity.getString(R.string.ok))
                        .setPositiveListener(new ClickListener() {
                            @Override
                            public void onClick(LottieAlertDialog lottieAlertDialog) {
                                lottieAlertDialog.dismiss();
                            }
                        })
                        .build();
                alertDialog.show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void custom_error(Activity activity,Object data) {
        try {
            if (!activity.isFinishing()) {
                if (!Objects.equals(checkNull(data), "")){
                    LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_ERROR)
                            .setTitle(activity.getString(R.string.oops))
                            .setDescription(data.toString())
                            .setPositiveText(activity.getString(R.string.ok))
                            .setPositiveListener(new ClickListener() {
                                @Override
                                public void onClick(LottieAlertDialog lottieAlertDialog) {
                                    lottieAlertDialog.dismiss();
                                }
                            })
                            .build();
                    alertDialog.show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public static void network_error_alert(final Activity activity) {
        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_ERROR)
                .setTitle(activity.getString(R.string.network_error))
                .setDescription(activity.getString(R.string.internet_connectivity_issue_occured_please_try_again))
                .setPositiveText(activity.getString(R.string.setting))
                .setNegativeText(activity.getString(R.string.ok))
                .setPositiveListener(new ClickListener() {
                    @Override
                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                        activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 1);
                        lottieAlertDialog.dismiss();
                    }
                }).setNegativeListener(new ClickListener() {
                    @Override
                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                        lottieAlertDialog.dismiss();
                    }
                })
                .build();
        alertDialog.show();
    }

    public static void loginPopup(final Activity activity) {
        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_WARNING)
                .setTitle(activity.getString(R.string.login_required))
                .setDescription(activity.getString(R.string.please_login_first))
                .setPositiveText(activity.getString(R.string.login))
                .setNegativeText(activity.getString(R.string.cancel))
                .setPositiveListener(new ClickListener() {
                    @Override
                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                        lottieAlertDialog.dismiss();
                    }
                }).setNegativeListener(new ClickListener() {
                    @Override
                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                        lottieAlertDialog.dismiss();
                    }
                })
                .build();
        alertDialog.show();
    }

    public static String getRightAngleImage(String photoPath) {

        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degree = 0;

            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    degree = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    degree = 0;
                    break;
                default:
                    degree = 90;
            }

            return rotateImage(degree, photoPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return photoPath;
    }

    public static String rotateImage(int degree, String imagePath) {

        if (degree <= 0) {
            return imagePath;
        }
        try {
            Bitmap b = BitmapFactory.decodeFile(imagePath);

            Matrix matrix = new Matrix();
            if (b.getWidth() > b.getHeight()) {
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
            }

            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            } else if (imageType.equalsIgnoreCase("jpeg") || imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();

            b.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    public static class StringWithTag {
        public String string;
        public Object tag;

        public StringWithTag(String stringPart, Object tagPart) {
            string = stringPart;
            tag = tagPart;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    public static class CountryModelName {
        public String countryName;
        public String countrySortName;
        public String countryCode;
        public Object tag;

        public CountryModelName(String countryName, String countrySortName, String countryCode, Object tagPart) {
            this.countryName = countryName;
            this.countrySortName = countrySortName;
            this.countryCode = countryCode;
            this.tag = tagPart;
        }

        @Override
        public String toString() {
            return countryName;
        }
    }

    public static class CountryModelName3 {
        public String shippingAllowed;
        public String countryName;
        public String countrySortName;
        public String countryCode;
        public Object tag;

        public CountryModelName3(String shippingAllowed,String countryName, String countrySortName, String countryCode, Object tagPart) {
            this.shippingAllowed = shippingAllowed;
            this.countryName = countryName;
            this.countrySortName = countrySortName;
            this.countryCode = countryCode;
            this.tag = tagPart;
        }

        @Override
        public String toString() {
            return countryName;
        }
    }

    public static class CountryModelName2 {
        public String countryName;
        public String countrySortName;
        public String countryCode;
        public String currencyCode;
        public String currencyConvert;
        public Object tag;

        public CountryModelName2(String countryName, String countrySortName, String countryCode, String currencyCode, String currencyConvert, Object tagPart) {
            this.countryName = countryName;
            this.countrySortName = countrySortName;
            this.countryCode = countryCode;
            this.currencyCode = currencyCode;
            this.currencyConvert = currencyConvert;
            this.tag = tagPart;
        }

        @Override
        public String toString() {
            return countryName;
        }
    }

/*    public static class CountryModelCode {
        public String countryName;
        public String countrySortName;
        public String countryCode;
        public Object tag;

        public CountryModelCode(String countryName, String countrySortName,String countryCode, Object tagPart) {
            this.countryName = countryName;
            this.countrySortName = countrySortName;
            this.countryCode = countryCode;
            this.tag = tagPart;
        }

        @Override
        public String toString() {
            return countryCode;
        }
    }*/


    public static class CountryModelCode implements Serializable {
        public String countryName;
        public String countrySortName;
        public String countryCode;
        public String tag;

        public CountryModelCode(String countryName, String countrySortName, String countryCode, String tagPart) {
            this.countryName = countryName;
            this.countrySortName = countrySortName;
            this.countryCode = countryCode;
            this.tag = tagPart;
        }

    }


    public static void Userlogout(final Activity activity) {
        if (!isNetworkConnected(activity)) {
            MethodClass.network_error_alert(activity);
            return;
        }
        MethodClass.showProgressDialog(activity);
        String server_url = activity.getString(R.string.SERVER_URL) + "logout";
        HashMap<String, String> params = new HashMap<String, String>();
        JSONObject jsonObject = MethodClass.Json_rpc_format(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(activity);
                Log.e("respLogout", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(activity, response);
                    if (resultResponse != null) {
                        signout(activity);
                    }
                } catch (Exception e) {
                    MethodClass.error_alert(activity);
                    e.printStackTrace();
                    Log.e("JSONException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.e("error2", error.getMessage());
                MethodClass.hideProgressDialog(activity);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(activity);
                } else {
                    MethodClass.error_alert(activity);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(activity).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(activity).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(activity).addToRequestQueue(jsonObjectRequest);
    }

    public static void signout(Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        if (mGoogleSignInClient != null) {
            mGoogleSignInClient.signOut();
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("is_logged_in", false).commit();
            Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(activity).getAll();
            for (Map.Entry<String, ?> prefToReset : prefs.entrySet()) {
                if (!prefToReset.getKey().equals("lang") && !prefToReset.getKey().equals("currency") && !prefToReset.getKey().equals("currency_code") && !prefToReset.getKey().equals("conv_factor") && !prefToReset.getKey().equals("country_id") && !prefToReset.getKey().equals("country_code") && !prefToReset.getKey().equals("is_first_time")) {
                    PreferenceManager.getDefaultSharedPreferences(activity).edit().remove(prefToReset.getKey()).commit();
                }
            }
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
        }

        CallbackManager callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(activity);
        if (AccessToken.getCurrentAccessToken() != null) {
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {
                    LoginManager.getInstance().logOut();
                }
            }).executeAsync();
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("is_logged_in", false).commit();
            Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(activity).getAll();
            for (Map.Entry<String, ?> prefToReset : prefs.entrySet()) {
                if (!prefToReset.getKey().equals("lang") && !prefToReset.getKey().equals("currency") && !prefToReset.getKey().equals("currency_code") && !prefToReset.getKey().equals("conv_factor") && !prefToReset.getKey().equals("country_id") && !prefToReset.getKey().equals("country_code") && !prefToReset.getKey().equals("is_first_time")) {
                    PreferenceManager.getDefaultSharedPreferences(activity).edit().remove(prefToReset.getKey()).commit();
                }
            }
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
        } else {
            PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean("is_logged_in", false).commit();
            Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(activity).getAll();
            for (Map.Entry<String, ?> prefToReset : prefs.entrySet()) {
                if (!prefToReset.getKey().equals("lang") && !prefToReset.getKey().equals("currency") && !prefToReset.getKey().equals("currency_code") && !prefToReset.getKey().equals("conv_factor") && !prefToReset.getKey().equals("country_id") && !prefToReset.getKey().equals("country_code") && !prefToReset.getKey().equals("is_first_time")) {
                    PreferenceManager.getDefaultSharedPreferences(activity).edit().remove(prefToReset.getKey()).commit();
                }
            }
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
        }
    }



     /* public  static boolean equalLists(List<String> one, List<String> two){
        if (one == null && two == null){
            return true;
        }

        if((one == null && two != null)
                || one != null && two == null
                || one.size() != two.size()){
            return false;
        }

        to avoid messing the order of the lists we will use a copy
        as noted in comments by A. R. S.
        one = new ArrayList<String>(one);
        two = new ArrayList<String>(two);

        Collections.sort(one);
        Collections.sort(two);

        Log.e("ONE", one.toString()+"                "+two.toString() );
        return one.equals(two);
        return one.containsAll(two) && two.containsAll(one);
    } */


    public static void shoExitDialog(final Activity activity) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.location_permisiion_popup);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.setCancelable(false);
        Button exit_btn = (Button) dialog.findViewById(R.id.exit_btn);
        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        dialog.show();
    }

    public static void setCartCount(Activity activity) {
        String cart_count = PreferenceManager.getDefaultSharedPreferences(activity).getString("cart_count", "");
        Log.e("cart=", cart_count);
        if (cart_count.equals("null") || cart_count.equals(null) || cart_count.equals("") || cart_count.equals("0")) {
            TextView cart_count_tv = activity.findViewById(R.id.cart_count_tv);
            cart_count_tv.setVisibility(View.GONE);
        } else {
            TextView cart_count_tv = activity.findViewById(R.id.cart_count_tv);
            cart_count_tv.setText(cart_count);
            cart_count_tv.setVisibility(View.VISIBLE);
        }
    }

    public static String checkNull(Object data) {
        if (Objects.equals(data, null) || Objects.equals(data, "null") || Objects.equals(data, "") || Objects.equals(data, "Select")) {
            return "";
        } else {
            return String.valueOf(data);
        }
    }

    public static String noFormat(double d) {
        d = noRound(d, 2);
        if (d == (long) d)
            return String.format(Locale.ENGLISH,"%d", (long) d);
        else
            return String.format(Locale.ENGLISH,"%s", d);
    }

    public static double noRound(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static void countryNamePopup2(Activity activity, ArrayList<CountryModelName2> countryArrayList, Spinner shi_spin_country) {
        ArrayList<CountryModelName2> popupCountryNameArray = new ArrayList<>();
        popupCountryNameArray.addAll(countryArrayList);
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.country_name_popup);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        EditText edit_search = dialog.findViewById(R.id.edit_search);
        RecyclerView country_recy = dialog.findViewById(R.id.country_recy);
        CountryNameRecy2Adapter recyAdapter = new CountryNameRecy2Adapter(activity, popupCountryNameArray, edit_search, dialog, shi_spin_country);
        country_recy.setAdapter(recyAdapter);
        dialog.show();
    }

    public static void countryCodePopup(Activity activity, ArrayList<CountryModelCode> countryArrayList, Spinner shi_spin_country) {
        ArrayList<CountryModelCode> popupCountryNameArray = new ArrayList<>();
        popupCountryNameArray.addAll(countryArrayList);
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.country_name_popup);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        EditText edit_search = dialog.findViewById(R.id.edit_search);
        RecyclerView country_recy = dialog.findViewById(R.id.country_recy);
        CountryCodeRecyAdapter recyAdapter = new CountryCodeRecyAdapter(activity, popupCountryNameArray, edit_search, dialog, shi_spin_country);
        country_recy.setAdapter(recyAdapter);
        dialog.show();
    }

    public static void countryNamePopup(Activity activity, ArrayList<CountryModelName> countryArrayList, Spinner shi_spin_country) {
        ArrayList<CountryModelName> popupCountryNameArray = new ArrayList<>();
        popupCountryNameArray.addAll(countryArrayList);
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.country_name_popup);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        EditText edit_search = dialog.findViewById(R.id.edit_search);
        RecyclerView country_recy = dialog.findViewById(R.id.country_recy);
        CountryNameRecyAdapter recyAdapter = new CountryNameRecyAdapter(activity, popupCountryNameArray, edit_search, dialog, shi_spin_country);
        country_recy.setAdapter(recyAdapter);
        dialog.show();
    }
    public static void countryNamePopup3(Activity activity, ArrayList<CountryModelName3> countryArrayList, Spinner shi_spin_country) {
        ArrayList<CountryModelName3> popupCountryNameArray = new ArrayList<>();
        popupCountryNameArray.addAll(countryArrayList);
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.country_name_popup);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        EditText edit_search = dialog.findViewById(R.id.edit_search);
        RecyclerView country_recy = dialog.findViewById(R.id.country_recy);
        CountryNameRecyAdapter3 recyAdapter = new CountryNameRecyAdapter3(activity, popupCountryNameArray, edit_search, dialog, shi_spin_country);
        country_recy.setAdapter(recyAdapter);
        dialog.show();
    }

    public static void smsRetriverMethod(Activity activity){
        SmsRetrieverClient client = SmsRetriever.getClient(activity);
        Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(activity, "sussess", Toast.LENGTH_SHORT).show();
                                /*Intent intent = new Intent("OTP");
                                LocalBroadcastManager.getInstance(VerifyActivity.this).sendBroadcast(intent);
                                // Successfully started retriever, expect broadcast intent
                                // ...*/
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever, inspect Exception for more details
                // ...
                Toast.makeText(activity, "Faided", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
