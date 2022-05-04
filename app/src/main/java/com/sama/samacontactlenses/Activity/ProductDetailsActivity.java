package com.sama.samacontactlenses.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;
import com.sama.samacontactlenses.Adapter.AvailableOfferAdapter;
import com.sama.samacontactlenses.Adapter.DetailsImageAdapter;
import com.sama.samacontactlenses.Adapter.ReviewAdapter;
import com.sama.samacontactlenses.Helper.MethodClass;
import com.sama.samacontactlenses.Helper.MySingleton;
import com.sama.samacontactlenses.R;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.ScaleRatingBar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.sama.samacontactlenses.Common.Constant.BANNER_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.CRAETED_AT;
import static com.sama.samacontactlenses.Common.Constant.IS_POSTED;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_DESC;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_ID;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_IMAGE_URL;
import static com.sama.samacontactlenses.Common.Constant.PRODUCT_TITLE;
import static com.sama.samacontactlenses.Common.Constant.RATING_START;
import static com.sama.samacontactlenses.Common.Constant.SPLASH_IMAGE_URL;
import static com.sama.samacontactlenses.Common.Constant.USER_NAME;
import static com.sama.samacontactlenses.Helper.MethodClass.isNetworkConnected;
import static com.sama.samacontactlenses.Helper.MethodClass.noFormat;

public class ProductDetailsActivity extends AppCompatActivity {
    private SliderView imageSlider;
    public static ArrayList<HashMap<String, String>> tagArrayListId_1;
    public static ArrayList<HashMap<String, String>> tagArrayListId_2;
    public static ArrayList<HashMap<String, String>> tagArrayListId_3;
    private RecyclerView recy_view_avail_off, recy_view_off_prod;
    private TextView withaignBtn, withpowerBtn, withoutPower;
    private LinearLayout withAsigma, withpOWER, optionLay;
    private TextView productTitle, subcategory, category, rating, price, totalPrice, description, optionPrice, description2;
    private ScaleRatingBar simpleRatingBar;
    private String aisgmaPrice, pwerPrice, mainPrice, mainPrice2;

    private String totPric, totPric2;
    private NestedScrollView scrollView;
    private TextView lensCategory, transmisibility, permAbility, thickness, diameter, baseCurve, wateContent, lensMate;
    private LinearLayout diameter_lay, base_curve_lay, water_content_lay, replacement_lay;
    private LinearLayout diameter_line, base_curve_line, water_content_line, replacement_line;
    private ImageView favBtn, share;

    private ArrayList<MethodClass.StringWithTag> powerArrayList;
    private TextView left_power_tv, right_power_tv;
    private LinearLayout diff_power_lay;
    private ImageView edit_with_power_img;
    private TextView addCart;
    private TextView plus_count_tv;
    private TextView minus_count_tv;
    private TextView count_tv;
    private TextView sphere;
    private int count = 1;

    private String withPower = "N";

    private LinearLayout generalSpec, lensSpec, offerLay, offerLineLay, availOffLayLine, availOffLay;
    private TextView review, lens, general;

    private LinearLayout review_layout;
    private RecyclerView recy_view_review;
    private Button post_reiew_btn;
    private TextView all_review_text;
    private TextView optionName;
    private TextView total_price;
    private TextView pack;

    private ArrayList<String> shp_array, cyl_array, ax_array, b_c_array;
    private ArrayList<String> r_shp_array, r_cyl_array, r_ax_array, r_b_c_array;

    String shp_str, cyl_str, ax_str, b_c_str, r_shp_str, r_cyl_str, r_ax_str, r_b_c_str;
    String left_power = "", right_power = "";
    private TextView shp_tv, cyl_tv, ax_tv, b_c_tv, r_shp_tv, r_cyl_tv, r_ax_tv, r_b_c_tv, discountPrice;
    private ImageView edit_astig;

    private boolean iswithPowerFirst = true;
    private boolean isAstigFirst = true;
    Dialog dialog;
    Dialog astig_dialog;

    private LinearLayout permAbility_lay, permAbility_line;
    private LinearLayout thickness_lay, thickness_line;
    private LinearLayout transmisibility_lay, transmisibility_line;
    private LinearLayout review_lay;

    private String discount_type, discount, discount_min_order_total, discount_on, offer_type, discount_valid_from, discount_valid_till;

    private LinearLayout mon_layout;
    private ImageView mon_image;
    private TextView mon_description;

    String agree_conditions = "";
    String image_url = "";
    boolean shouldAlertForAddToCart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        //MethodClass.set_locale(this);
        setContentView(R.layout.activity_product_details);
        MethodClass.setBottomFun(this);
        imageSlider = findViewById(R.id.imageSlider);
        withaignBtn = findViewById(R.id.withaignBtn);
        withpowerBtn = findViewById(R.id.withpowerBtn);
        productTitle = findViewById(R.id.productTitle);
        subcategory = findViewById(R.id.subcategory);
        discountPrice = findViewById(R.id.discountPrice);
        category = findViewById(R.id.category);
        simpleRatingBar = findViewById(R.id.simpleRatingBar);
        recy_view_avail_off = findViewById(R.id.recy_view_avail_off);
        recy_view_off_prod = findViewById(R.id.recy_view_off_prod);
        availOffLay = findViewById(R.id.availOffLay);
        availOffLayLine = findViewById(R.id.availOffLayLine);
        offerLay = findViewById(R.id.offerLay);
        offerLineLay = findViewById(R.id.offerLineLay);
        rating = findViewById(R.id.rating);
        price = findViewById(R.id.price);
        totalPrice = findViewById(R.id.totalPrice);
        description = findViewById(R.id.description);
        scrollView = findViewById(R.id.scrollView);
        recy_view_review = findViewById(R.id.recy_view_review);
        review_layout = findViewById(R.id.review_layout);
        post_reiew_btn = findViewById(R.id.post_reiew_btn);
        all_review_text = findViewById(R.id.all_review_text);

        withAsigma = findViewById(R.id.withAsigma);
        withpOWER = findViewById(R.id.withpOWER);
        optionLay = findViewById(R.id.optionLay);
        optionPrice = findViewById(R.id.optionPrice);
        favBtn = findViewById(R.id.favBtn);
        share = findViewById(R.id.share);
        pack = findViewById(R.id.pack);
        total_price = findViewById(R.id.total_price);
        optionName = findViewById(R.id.optionName);
        edit_astig = findViewById(R.id.edit_astig);

        shp_tv = findViewById(R.id.shp_tv);
        cyl_tv = findViewById(R.id.cyl_tv);
        ax_tv = findViewById(R.id.ax_tv);
        b_c_tv = findViewById(R.id.b_c_tv);
        r_shp_tv = findViewById(R.id.r_shp_tv);
        r_cyl_tv = findViewById(R.id.r_cyl_tv);
        r_ax_tv = findViewById(R.id.r_ax_tv);
        r_b_c_tv = findViewById(R.id.r_b_c_tv);


        lensMate = findViewById(R.id.lensMate);
        baseCurve = findViewById(R.id.baseCurve);
        wateContent = findViewById(R.id.wateContent);
        permAbility = findViewById(R.id.permAbility);
        transmisibility = findViewById(R.id.transmisibility);
        thickness = findViewById(R.id.thickness);
        lensCategory = findViewById(R.id.lensCategory);
        //clearity = findViewById(R.id.clearity);
        diameter = findViewById(R.id.diameter);
        withoutPower = findViewById(R.id.withoutPower);
        left_power_tv = findViewById(R.id.left_power_tv);
        right_power_tv = findViewById(R.id.right_power_tv);
        diff_power_lay = findViewById(R.id.diff_power_lay);
        plus_count_tv = findViewById(R.id.plus_count_tv);
        minus_count_tv = findViewById(R.id.minus_count_tv);
        count_tv = findViewById(R.id.count_tv);
        addCart = findViewById(R.id.addCart);
        lensSpec = findViewById(R.id.lensSpec);
        generalSpec = findViewById(R.id.generalSpec);
        review = findViewById(R.id.review);
        lens = findViewById(R.id.lens);
        general = findViewById(R.id.general);
        description2 = findViewById(R.id.description2);
        sphere = findViewById(R.id.sphere);
        edit_with_power_img = findViewById(R.id.edit_with_power_img);

        permAbility_lay = findViewById(R.id.permAbility_lay);
        permAbility_line = findViewById(R.id.permAbility_line);
        thickness_lay = findViewById(R.id.thickness_lay);
        thickness_line = findViewById(R.id.thickness_line);
        transmisibility_lay = findViewById(R.id.transmisibility_lay);
        transmisibility_line = findViewById(R.id.transmisibility_line);

        replacement_lay = findViewById(R.id.replacement_lay);
        replacement_line = findViewById(R.id.replacement_line);
        water_content_lay = findViewById(R.id.water_content_lay);
        water_content_line = findViewById(R.id.water_content_line);
        base_curve_lay = findViewById(R.id.base_curve_lay);
        base_curve_line = findViewById(R.id.base_curve_line);
        diameter_lay = findViewById(R.id.diameter_lay);
        diameter_line = findViewById(R.id.diameter_line);

        review_lay = findViewById(R.id.review_lay);


        mon_layout = findViewById(R.id.mon_layout);
        mon_image = findViewById(R.id.mon_image);
        mon_description = findViewById(R.id.mon_description);

        count_tv.setText(String.valueOf(count));

        general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generalSpec.setVisibility(View.VISIBLE);
                lensSpec.setVisibility(View.GONE);
                review_layout.setVisibility(View.GONE);
                general.setBackground(getResources().getDrawable(R.drawable.rect_grey));
                lens.setBackgroundColor(getResources().getColor(R.color.transparent));
                review.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        });
        lens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generalSpec.setVisibility(View.GONE);
                lensSpec.setVisibility(View.VISIBLE);
                review_layout.setVisibility(View.GONE);
                general.setBackgroundColor(getResources().getColor(R.color.transparent));
                lens.setBackground(getResources().getDrawable(R.drawable.rect_grey));
                review.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        });
        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generalSpec.setVisibility(View.GONE);
                lensSpec.setVisibility(View.GONE);
                review_layout.setVisibility(View.VISIBLE);
                general.setBackgroundColor(getResources().getColor(R.color.transparent));
                review.setBackground(getResources().getDrawable(R.drawable.rect_grey));
                lens.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        });

        edit_with_power_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withpowerBtn.performClick();
            }
        });

        withoutPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                withoutPower.setText(R.string.right_without_power);
                withoutPower.setBackground(getDrawable(R.drawable.light_grad));
                withpowerBtn.setText(getString(R.string.WithP));
                withpowerBtn.setBackground(getDrawable(R.drawable.dark_grey_btn));
                withaignBtn.setText(getString(R.string.with_astigmatism));
                withaignBtn.setBackground(getDrawable(R.drawable.dark_grey_btn));
                withpOWER.setVisibility(View.GONE);
                withAsigma.setVisibility(View.GONE);
                optionLay.setVisibility(View.GONE);
                totalPrice.setText(price.getText().toString());
                totPric = String.format(Locale.ENGLISH, "%.3f", (Double.parseDouble(mainPrice)));
                totalPrice.setText(noFormat((Double.parseDouble(mainPrice) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", "")))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat((Double.parseDouble(mainPrice) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                withPower = "N";
                getPrice();

            }
        });

        withpowerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iswithPowerFirst) {
                    iswithPowerFirst = false;
                    dialog = new Dialog(ProductDetailsActivity.this);
                    dialog.setContentView(R.layout.power_popup);
                    Window window = dialog.getWindow();
                    window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    Spinner left_power_spin = (Spinner) dialog.findViewById(R.id.left_power_spin);
                    Spinner right_power_spin = (Spinner) dialog.findViewById(R.id.right_power_spin);
                    Button save = (Button) dialog.findViewById(R.id.save);
                    LinearLayout doubleLay = (LinearLayout) dialog.findViewById(R.id.doubleLay);
                    ImageView close = (ImageView) dialog.findViewById(R.id.close);

                    ArrayAdapter<MethodClass.StringWithTag> left_adapter = new ArrayAdapter<MethodClass.StringWithTag>(ProductDetailsActivity.this, android.R.layout.simple_dropdown_item_1line, powerArrayList);
                    left_power_spin.setAdapter(left_adapter);
                    left_power_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            MethodClass.StringWithTag stringWithTag = (MethodClass.StringWithTag) parent.getItemAtPosition(position);
                            if (position == 0) {
                                left_power = "Select";
                            } else {
                                left_power = (String) stringWithTag.string;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    ArrayAdapter<MethodClass.StringWithTag> right_adapter = new ArrayAdapter<MethodClass.StringWithTag>(ProductDetailsActivity.this, android.R.layout.simple_dropdown_item_1line, powerArrayList);
                    right_power_spin.setAdapter(right_adapter);
                    right_power_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            MethodClass.StringWithTag stringWithTag = (MethodClass.StringWithTag) parent.getItemAtPosition(position);
                            if (position == 0) {
                                right_power = "Select";
                            } else {
                                right_power = (String) stringWithTag.string;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (left_power.equals("Select") && right_power.equals("Select")) {
                                Toast.makeText(ProductDetailsActivity.this, getString(R.string.please_select_power), Toast.LENGTH_SHORT).show();
                            } else {
                                withPower = "Y";
                                getPrice();
                                dialog.dismiss();
                                shouldAlertForAddToCart = false;
                            }
                        }
                    });
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    dialog.show();
                }

            }
        });

        edit_astig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withaignBtn.performClick();
            }
        });

        withaignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAstigFirst) {
                    isAstigFirst = false;
                    astig_dialog = new Dialog(ProductDetailsActivity.this);
                    astig_dialog.setContentView(R.layout.asigmatism_popup);
                    Window window = astig_dialog.getWindow();
                    window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    Spinner shp_spin = (Spinner) astig_dialog.findViewById(R.id.shp_spin);
                    Spinner cyl_spin = (Spinner) astig_dialog.findViewById(R.id.cyl_spin);
                    Spinner ax_spin = (Spinner) astig_dialog.findViewById(R.id.ax_spin);
                    Spinner b_c_spin = (Spinner) astig_dialog.findViewById(R.id.b_c_spin);

                    Spinner r_shp_spin = (Spinner) astig_dialog.findViewById(R.id.r_shp_spin);
                    Spinner r_cyl_spin = (Spinner) astig_dialog.findViewById(R.id.r_cyl_spin);
                    Spinner r_ax_spin = (Spinner) astig_dialog.findViewById(R.id.r_ax_spin);
                    Spinner r_b_c_spin = (Spinner) astig_dialog.findViewById(R.id.r_b_c_spin);
                    Button save = (Button) astig_dialog.findViewById(R.id.save);
                    ImageView close = (ImageView) astig_dialog.findViewById(R.id.close);
                    CheckBox astigmetism_chk = (CheckBox) astig_dialog.findViewById(R.id.astigmetism_chk);
                    TextView aggree = astig_dialog.findViewById(R.id.aggree);
                    aggree.setText(agree_conditions);


                    ArrayAdapter<String> shp_adapter = new ArrayAdapter<String>(ProductDetailsActivity.this, android.R.layout.simple_dropdown_item_1line, shp_array);
                    shp_spin.setAdapter(shp_adapter);
                    shp_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                shp_str = "Select";
                            } else {
                                shp_str = shp_array.get(position);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    r_shp_spin.setAdapter(shp_adapter);
                    r_shp_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                r_shp_str = "Select";
                            } else {
                                r_shp_str = shp_array.get(position);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    ArrayAdapter<String> cyl_adapter = new ArrayAdapter<String>(ProductDetailsActivity.this, android.R.layout.simple_dropdown_item_1line, cyl_array);
                    cyl_spin.setAdapter(cyl_adapter);
                    cyl_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                cyl_str = "Select";
                            } else {
                                cyl_str = cyl_array.get(position);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    r_cyl_spin.setAdapter(cyl_adapter);
                    r_cyl_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                r_cyl_str = "Select";
                            } else {
                                r_cyl_str = cyl_array.get(position);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    ArrayAdapter<String> ax_adapter = new ArrayAdapter<String>(ProductDetailsActivity.this, android.R.layout.simple_dropdown_item_1line, ax_array);
                    ax_spin.setAdapter(ax_adapter);
                    ax_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                ax_str = "Select";
                            } else {
                                ax_str = ax_array.get(position);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    r_ax_spin.setAdapter(ax_adapter);
                    r_ax_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                r_ax_str = "Select";
                            } else {
                                r_ax_str = ax_array.get(position);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    ArrayAdapter<String> b_c_adapter = new ArrayAdapter<String>(ProductDetailsActivity.this, android.R.layout.simple_dropdown_item_1line, b_c_array);
                    b_c_spin.setAdapter(b_c_adapter);
                    b_c_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                b_c_str = "Select";
                            } else {
                                b_c_str = b_c_array.get(position);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    r_b_c_spin.setAdapter(b_c_adapter);
                    r_b_c_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                r_b_c_str = "Select";
                            } else {
                                r_b_c_str = b_c_array.get(position);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (shp_str.equals("Select") && r_shp_str.equals("Select") && cyl_str.equals("Select") && r_cyl_str.equals("Select") && ax_str.equals("Select") && r_ax_str.equals("Select") && b_c_str.equals("Select") && r_b_c_str.equals("Select")) {
                                Toast.makeText(ProductDetailsActivity.this, "Please select astigmatism", Toast.LENGTH_SHORT).show();
                            } else {
                                if (astigmetism_chk.isChecked()) {
                                    withPower = "A";
                                    getPrice();
                                    astig_dialog.dismiss();
                                    shouldAlertForAddToCart = false;
                                } else {
                                    Toast.makeText(ProductDetailsActivity.this, "Please check term and condition", Toast.LENGTH_SHORT).show();
                                }
                            }



                            /*withPower = "A";
                             *//*withaignBtn.setText(R.string.right_with_astigma);
                        withaignBtn.setBackground(getDrawable(R.drawable.light_grad));
                        withpowerBtn.setText(getString(R.string.WithP));
                        withpowerBtn.setBackground(getDrawable(R.drawable.dark_grey_btn));
                        withAsigma.setVisibility(View.VISIBLE);
                        withpOWER.setVisibility(View.GONE);
                        optionLay.setVisibility(View.VISIBLE);
                        optionPrice.setText(aisgmaPrice +" "+PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code","AED"));
                        optionName.setText(getString(R.string.astigPrice));
                        totalPrice.setText(String.format(Locale.ENGLISH, "%.3f", (Double.parseDouble(mainPrice) + Double.parseDouble(aisgmaPrice))) +" "+PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code","AED"));
                        total_price.setText(String.valueOf(count) + " item | " + String.format(Locale.ENGLISH, "%.3f", (Double.parseDouble(mainPrice) + Double.parseDouble(aisgmaPrice)) * count) +" "+PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code","AED"));
                        *//*
                            getPrice();
                            astig_dialog.dismiss();*/
                        }
                    });
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            astig_dialog.dismiss();
                        }
                    });
                    astig_dialog.show();
                } else {
                    astig_dialog.show();
                }

            }
        });

        plus_count_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = count + 1;
                count_tv.setText(String.valueOf(count));
                total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat((Double.parseDouble(totPric) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
            }
        });

        minus_count_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count > 1) {
                    count = count - 1;
                    count_tv.setText(String.valueOf(count));
                    total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat((Double.parseDouble(totPric) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                }
            }
        });

        addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shouldAlertForAddToCart) {
                    LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(ProductDetailsActivity.this, DialogTypes.TYPE_ERROR)
                            .setTitle("Alert")
                            .setDescription("Please select Power to continue")
                            .setPositiveText("Ok")
                            .setPositiveListener(new ClickListener() {
                                @Override
                                public void onClick(LottieAlertDialog lottieAlertDialog) {
                                    lottieAlertDialog.dismiss();

                                }
                            })
                            .build();
                    alertDialog.show();

                } else
                add_to_Cart();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MethodClass.share(ProductDetailsActivity.this, productTitle.getText().toString(), getIntent().getStringExtra("product_id"), PRODUCT_IMAGE_URL + image_url);
            }
        });

        getProduct();
    }

    public void getProduct() {
        if (!isNetworkConnected(this)) {
            MethodClass.network_error_alert(this);
            return;
        }
        MethodClass.showProgressDialog(this);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = getString(R.string.SERVER_URL) + "product-details";
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("product_id", getIntent().getStringExtra("product_id"));

        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("CATGOEY", new JSONObject(params).toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MethodClass.hideProgressDialog(ProductDetailsActivity.this);
                Log.e("respUser", response.toString());
                try {
                    JSONObject resultResponse = MethodClass.get_result_from_webservice(ProductDetailsActivity.this, response);
                    if (resultResponse != null) {

                        JSONObject product = resultResponse.getJSONObject("product").getJSONObject("product_details");
                        tagArrayListId_2 = new ArrayList<HashMap<String, String>>();
                        /*JSONArray offerProducts = resultResponse.getJSONObject("product").getJSONArray("offerProducts");
                        if(offerProducts.length()>0){

                            tagArrayListId_3 = new ArrayList<HashMap<String, String>>();

                            for (int i = 0; i <offerProducts.length() ; i++) {
                                JSONObject offerproduct = offerProducts.getJSONObject(i).getJSONObject("offerproduct");
                                HashMap<String, String> map = new HashMap<String, String>();
                                String id = offerproduct.getString("id");
                                String price_without_power = offerproduct.getString("price_without_power");
                                JSONObject default_image = offerproduct.getJSONObject("default_image");
                                String image = default_image.getString("image");
                                //JSONObject product_by_language = offerproduct.getJSONObject("product_by_language");
                                String title = offerProducts.getJSONObject(i).getJSONObject("offer_product_by_language").getString("title");
                                String description = "";
                                if(!offerproduct.getString("product_sub_category").equals("null")){
                                    JSONObject product_sub_category = offerproduct.getJSONObject("product_sub_category");

                                    description = product_sub_category.getJSONObject("category").getJSONObject("category_by_language").getString("title");

                                }else {
                                    JSONObject product_sub_category = offerproduct.getJSONObject("product_parent_category");

                                    description = product_sub_category.getJSONObject("category").getJSONObject("category_by_language").getString("title");

                                }
                                String offer_type = offerproduct.getString("offer_type");
                                String discount = offerproduct.getString("discount");
                                String discount_type = offerproduct.getString("discount_type");
                                String discount_valid_from = offerproduct.getString("discount_valid_from");
                                String discount_valid_till = offerproduct.getString("discount_valid_till");

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date dCurrentDate = null;
                                Date toDate = null;
                                Date fromDate = null;
                                try {
                                    fromDate = dateFormat.parse(discount_valid_from);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    toDate = dateFormat.parse(discount_valid_till);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    dCurrentDate = dateFormat.parse(dateFormat.format(new Date()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if ((dCurrentDate.equals(fromDate) || dCurrentDate.after(fromDate)) && (dCurrentDate.before(toDate) || dCurrentDate.equals(toDate))) {
                                    if(!offer_type.equals("null")){
                                        map.put(PRODUCT_OFF, offer_type);
                                        map.put(PRODUCT_DISC, discount);
                                        map.put(PRODUCT_DISC_TYPE, discount_type);
                                    }else {
                                        map.put(PRODUCT_OFF, "N");
                                    }

                                }else {
                                    map.put(PRODUCT_OFF, "N");
                                }
                                map.put(PRODUCT_ID, id);
                                map.put(PRODUCT_TITLE, title);
                                map.put(PRODUCT_PRICE, price_without_power);
                                map.put(PRODUCT_DESC, description);
                                map.put(PRODUCT_IMAGE, image);
                                tagArrayListId_3.add(map);
                            }
                            OfferAdapter adapterss = new OfferAdapter(ProductDetailsActivity.this, tagArrayListId_3);
                            recy_view_off_prod.setAdapter(adapterss);

                            offerLineLay.setVisibility(View.VISIBLE);
                            offerLay.setVisibility(View.VISIBLE);
                            availOffLay.setVisibility(View.VISIBLE);
                            availOffLayLine.setVisibility(View.VISIBLE);

                        }else {
                            offerLineLay.setVisibility(View.GONE);
                            offerLay.setVisibility(View.GONE);
                            availOffLay.setVisibility(View.GONE);
                            availOffLayLine.setVisibility(View.GONE);
                        }*/
                        JSONArray images = product.getJSONArray("images");
                        image_url = images.getJSONObject(0).getString("image");
                        tagArrayListId_1 = new ArrayList<>();
                        for (int i = 0; i < images.length(); i++) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            String image = images.getJSONObject(i).getString("image");
                            map.put(BANNER_IMAGE, image);
                            tagArrayListId_1.add(map);
                        }
                        DetailsImageAdapter adapter = new DetailsImageAdapter(ProductDetailsActivity.this, tagArrayListId_1);
                        imageSlider.setSliderAdapter(adapter);
                        imageSlider.startAutoCycle();
                        imageSlider.setCurrentPageListener(new SliderView.OnSliderPageListener() {
                            @Override
                            public void onSliderPageChanged(int position) {
                                imageSlider.stopAutoCycle();
                                imageSlider.startAutoCycle();
                            }
                        });
                        productTitle.setText(product.getJSONObject("product_by_language").getString("title"));

                        String title = product.getJSONObject("product_by_language").getString("title");

                        description.setText(product.getJSONObject("product_by_language").getString("description"));
                        description2.setText(product.getJSONObject("product_by_language").getString("description"));
                        if (!product.getString("product_sub_category").equals("null")) {
                            subcategory.setText(product.getJSONObject("product_sub_category").getJSONObject("category").getJSONObject("category_by_language").getString("title"));
                            subcategory.setVisibility(View.VISIBLE);
                        } else {
                            subcategory.setVisibility(View.GONE);
                        }

                        category.setText(product.getJSONObject("product_parent_category").getJSONObject("category").getJSONObject("category_by_language").getString("title"));
                        lensCategory.setText(product.getJSONObject("product_parent_category").getJSONObject("category").getJSONObject("category_by_language").getString("title"));

                        if (product.getString("power_type_with_astigmatism").equals("Y")) {
                            withaignBtn.setVisibility(View.VISIBLE);
                            aisgmaPrice = product.getString("price_astigmatism");
                            shouldAlertForAddToCart = true;
                        } else {
                            withaignBtn.setVisibility(View.GONE);
                        }

                        if (product.getString("power_type_with_power").equals("Y")) {
                            withpowerBtn.setVisibility(View.VISIBLE);
                            pwerPrice = product.getString("price_with_power");
                            shouldAlertForAddToCart = true;
                        } else {
                            withpowerBtn.setVisibility(View.GONE);
                        }

                        if (product.getString("power_type_no_power").equals("Y")){
                            withoutPower.setVisibility(View.VISIBLE);
                            shouldAlertForAddToCart = false;
                        } else {
                            withoutPower.setVisibility(View.GONE);
                        }

                        /*if (product.getString("power_type_with_astigmatism").equals("Y") && product.getString("power_type_with_power").equals("Y")) {
                            withpowerBtn.setVisibility(View.VISIBLE);
                            withoutPower.setVisibility(View.VISIBLE);
                            withaignBtn.setVisibility(View.VISIBLE);
                        } else {
                            if (product.getString("power_type_with_power").equals("Y")){
                                withpowerBtn.setVisibility(View.VISIBLE);
                            } else {
                                withpowerBtn.setVisibility(View.GONE);
                            }

                            if (product.getString("power_type_with_astigmatism").equals("Y")){
                                withaignBtn.setVisibility(View.VISIBLE);
                                shouldAlertForAddToCart = false;
                            } else {
                                withaignBtn.setVisibility(View.GONE);
                                shouldAlertForAddToCart = true;
                            }

                            if (product.getString("power_type_no_power").equals("Y")){
                                withoutPower.setVisibility(View.VISIBLE);
                                shouldAlertForAddToCart = false;
                            } else {
                                withoutPower.setVisibility(View.GONE);
                                shouldAlertForAddToCart = true;
                            }

                        }*/

                        if (product.getString("quantity_on_box").equals("0")) {
                            pack.setVisibility(View.GONE);
                        } else {
                            pack.setVisibility(View.VISIBLE);
                            pack.setText(getString(R.string.packof) + " " + product.getString("quantity_on_box"));
                        }

                        price.setText((noFormat(Double.parseDouble(product.getString("price_without_power")) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", "")))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));

                        mainPrice2 = product.getString("price_without_power");
                        offer_type = product.getString("offer_type");
                        discount_type = product.getString("discount_type");
                        discount = product.getString("discount");
                        discount_min_order_total = product.getString("discount_min_order_total");
                        discount_valid_from = product.getString("discount_valid_from");
                        discount_valid_till = product.getString("discount_valid_till");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date dCurrentDate = null;
                        Date toDate = null;
                        Date fromDate = null;
                        try {
                            fromDate = dateFormat.parse(discount_valid_from);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        try {
                            toDate = dateFormat.parse(discount_valid_till);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        try {
                            dCurrentDate = dateFormat.parse(dateFormat.format(new Date()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if ((dCurrentDate.equals(fromDate) || dCurrentDate.after(fromDate)) && (dCurrentDate.before(toDate) || dCurrentDate.equals(toDate))) {
                            if (offer_type.equals("B") || offer_type.equals("D")) {
                                HashMap<String, String> maps = new HashMap<String, String>();
                                if (!discount_min_order_total.equals("null") && Float.parseFloat(discount_min_order_total) > 0) {
                                    if (discount_type.equals("F")) {
                                        String varian = "";
                                        discount_on = product.getString("discount_on");
                                        if (!discount_on.equals("null")) {
                                            JSONArray discOn = new JSONArray(discount_on);
                                            for (int i = 0; i < discOn.length(); i++) {
                                                if (discOn.getJSONObject(i).getString("power").equals("NP")) {
                                                    varian = "No Power";
                                                }
                                                if (discOn.getJSONObject(i).getString("power").equals("WP")) {
                                                    varian = varian + ",With Power";
                                                }
                                                if (discOn.getJSONObject(i).getString("power").equals("WA")) {
                                                    varian = varian + ",With Asigmatism";
                                                }
                                            }
                                            Double D = Double.parseDouble(discount);
                                            Double conv_fac = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                                            String curr_code = PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED");
                                            maps.put(PRODUCT_TITLE, "Flat " + noFormat(D * conv_fac) + " " + curr_code + " discount on minimum order total of " + noFormat(Double.parseDouble(discount_min_order_total) * conv_fac) + " " + curr_code + " variant " + varian);
                                        } else {
                                            Double D = Double.parseDouble(discount);
                                            Double conv_fac = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                                            String curr_code = PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED");
                                            maps.put(PRODUCT_TITLE, "Flat " + noFormat(D * conv_fac) + " " + curr_code + " discount on minimum order total of " + noFormat(Double.parseDouble(discount_min_order_total) * conv_fac) + " " + curr_code);
                                        }
                                        tagArrayListId_2.add(maps);
                                    }
                                    if (discount_type.equals("P")) {
                                        String varian = "";
                                        discount_on = product.getString("discount_on");
                                        if (!discount_on.equals("null")) {
                                            JSONArray discOn = new JSONArray(discount_on);
                                            for (int i = 0; i < discOn.length(); i++) {
                                                if (discOn.getJSONObject(i).getString("power").equals("NP")) {
                                                    varian = "No Power";
                                                }
                                                if (discOn.getJSONObject(i).getString("power").equals("WP")) {
                                                    varian = varian + ",With Power";
                                                }
                                                if (discOn.getJSONObject(i).getString("power").equals("WA")) {
                                                    varian = varian + ",With Asigmatism";
                                                }
                                            }
                                            Double D = Double.parseDouble(discount);
                                            Double conv_fac = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                                            String curr_code = PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED");
                                            maps.put(PRODUCT_TITLE, noFormat(D) + " % discount on minimum order total of " + noFormat(Double.parseDouble(discount_min_order_total) * conv_fac) + " " + curr_code + " variant " + varian);
                                        } else {
                                            Double D = Double.parseDouble(discount);
                                            Double conv_fac = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                                            String curr_code = PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED");
                                            maps.put(PRODUCT_TITLE, noFormat(D) + " % discount on minimum order total of " + noFormat(Double.parseDouble(discount_min_order_total) * conv_fac) + " " + curr_code);
                                        }
                                        tagArrayListId_2.add(maps);
                                    }
                                } else {
                                    if (discount_type.equals("F")) {

                                        String varian = "";
                                        discount_on = product.getString("discount_on");
                                        if (!discount_on.equals("null")) {
                                            JSONArray discOn = new JSONArray(discount_on);
                                            for (int i = 0; i < discOn.length(); i++) {
                                                if (discOn.getJSONObject(i).getString("power").equals("NP")) {
                                                    varian = "No Power";
                                                }
                                                if (discOn.getJSONObject(i).getString("power").equals("WP")) {
                                                    varian = varian + ",With Power";
                                                }
                                                if (discOn.getJSONObject(i).getString("power").equals("WA")) {
                                                    varian = varian + ",With Asigmatism";
                                                }
                                            }
                                            Double D = Double.parseDouble(discount);
                                            Double conv_fac = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                                            String curr_code = PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED");
                                            maps.put(PRODUCT_TITLE, "Flat " + noFormat(D * conv_fac) + " " + curr_code + " discount on variant " + varian);
                                        } else {
                                            Double D = Double.parseDouble(discount);
                                            Double conv_fac = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                                            String curr_code = PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED");
                                            maps.put(PRODUCT_TITLE, "Flat " + noFormat(D * conv_fac) + " " + curr_code + " discount");
                                        }
                                        tagArrayListId_2.add(maps);
                                    }
                                    if (discount_type.equals("P")) {
                                        Log.e("DISC", discount_type);
                                        String varian = "";
                                        discount_on = product.getString("discount_on");
                                        if (!discount_on.equals("null")) {
                                            JSONArray discOn = new JSONArray(discount_on);
                                            for (int i = 0; i < discOn.length(); i++) {
                                                if (discOn.getJSONObject(i).getString("power").equals("NP")) {
                                                    varian = "No Power";
                                                }
                                                if (discOn.getJSONObject(i).getString("power").equals("WP")) {
                                                    varian = varian + ",With Power";
                                                }
                                                if (discOn.getJSONObject(i).getString("power").equals("WA")) {
                                                    varian = varian + ",With Astigmatism";
                                                }
                                            }
                                            Double D = Double.parseDouble(discount);
                                            maps.put(PRODUCT_TITLE, noFormat(D) + " % discount on variant " + varian);
                                            Log.e("DISC", noFormat(D) + " % discount on variant " + varian);
                                        } else {
                                            Double D = Double.parseDouble(discount);
                                            maps.put(PRODUCT_TITLE, noFormat(D) + " % discount");
                                        }
                                        tagArrayListId_2.add(maps);

                                    }
                                }


                            }
                        } else {

                        }


                        /*JSONArray offerBuyXGetY=resultResponse.getJSONObject("product").getJSONArray("offerBuyXGetY");
                        String freeValue=resultResponse.getJSONObject("product").getString("freeValue");
                         for (int i = 0; i <offerBuyXGetY.length() ; i++) {
                            HashMap<String,String> map = new HashMap<String, String>();
                            String buy_quantity = offerBuyXGetY.getJSONObject(i).getString("buy_quantity");
                            String free_quantity = offerBuyXGetY.getJSONObject(i).getString("free_quantity");
                            String offer_valid_from = offerBuyXGetY.getJSONObject(i).getString("offer_valid_from");
                            String offer_valid_to = offerBuyXGetY.getJSONObject(i).getString("offer_valid_to");
                             //Buy 4 of this lenses get 2 free lens worth less than 499 AED
                            map.put(PRODUCT_TITLE,"Buy "+buy_quantity+" of this lenses get "+free_quantity+" free lens worth less than "+freeValue+" AED");
                            tagArrayListId_2.add(map);
                        }*/
                        /*for (int i = 0; i <offerProducts.length() ; i++) {
                            HashMap<String,String> map = new HashMap<String, String>();
                            String by_quantity = offerProducts.getJSONObject(i).getString("by_quantity");
                            String offer_quantity = offerProducts.getJSONObject(i).getString("offer_quantity");
                            String product_by_language = offerProducts.getJSONObject(i).getJSONObject("product_by_language").getString("title");
                            String offer_product_by_language = offerProducts.getJSONObject(i).getJSONObject("offer_product_by_language").getString("title");

                            map.put(PRODUCT_TITLE,"Buy "+by_quantity+" "+product_by_language+" lens(es) to get "+offer_quantity+" "+offer_product_by_language+" lens(es) free");
                            tagArrayListId_2.add(map);

                        }*/
                        Log.e("TAGLIST", tagArrayListId_2.toString());
                        AvailableOfferAdapter adapterAd = new AvailableOfferAdapter(ProductDetailsActivity.this, tagArrayListId_2);
                        recy_view_avail_off.setAdapter(adapterAd);
                        if (!tagArrayListId_2.isEmpty()) {
                            availOffLay.setVisibility(View.VISIBLE);
                            availOffLayLine.setVisibility(View.VISIBLE);
                        } else {
                            availOffLay.setVisibility(View.GONE);
                            availOffLayLine.setVisibility(View.GONE);
                        }
                        if ((dCurrentDate.equals(fromDate) || dCurrentDate.after(fromDate)) && (dCurrentDate.before(toDate) || dCurrentDate.equals(toDate))) {
                            if (!discount_min_order_total.equals("null") && Float.parseFloat(discount_min_order_total) > 0) {
                                totalPrice.setText((noFormat(Double.parseDouble(product.getString("price_without_power")) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED")));

                                total_price.setText("1 " + getString(R.string.item_with) + (noFormat(Double.parseDouble(product.getString("price_without_power")) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED")));

                                totPric = product.getString("price_without_power");
                                mainPrice = product.getString("price_without_power");
                                discountPrice.setText("0 " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                            } else if (offer_type.equals("B") || offer_type.equals("D")) {
                                if (discount_type.equals("F")) {
                                    String varian = "";
                                    discount_on = product.getString("discount_on");
                                    if (!discount_on.equals("null")) {
                                        JSONArray discOn = new JSONArray(discount_on);
                                        for (int i = 0; i < discOn.length(); i++) {
                                            if (discOn.getJSONObject(i).getString("power").equals("NP")) {
                                                varian = "Y";

                                            }

                                        }
                                        if (varian.equals("Y")) {
                                            Double D = Double.parseDouble(product.getString("price_without_power")) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                                            Double E = Double.parseDouble(discount) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                                            Double pri = D - E;
                                            String dis = noFormat(pri);
                                            totalPrice.setText(String.valueOf(dis) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            discountPrice.setText(noFormat(E) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            total_price.setText("1 " + getString(R.string.item_with) + String.valueOf(dis) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            totPric = String.valueOf(dis);
                                            mainPrice = String.valueOf(dis);
                                        } else {
                                            totalPrice.setText((noFormat(Double.parseDouble(product.getString("price_without_power")) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED")));

                                            total_price.setText("1 " + getString(R.string.item_with) + (noFormat(Double.parseDouble(product.getString("price_without_power")) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED")));

                                            totPric = product.getString("price_without_power");
                                            mainPrice = product.getString("price_without_power");
                                            discountPrice.setText("0 " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                        }
                                    } else {
                                        Double D = Double.parseDouble(product.getString("price_without_power")) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                                        Double E = Double.parseDouble(discount) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                                        Double pri = D - E;
                                        String dis = noFormat(pri);
                                        totalPrice.setText(String.valueOf(dis) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                        discountPrice.setText(noFormat(E) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                        total_price.setText("1 " + getString(R.string.item_with) + String.valueOf(dis) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                        totPric = String.valueOf(dis);
                                        mainPrice = String.valueOf(dis);
                                    }
                                }
                                if (discount_type.equals("P")) {
                                    String varian = "";
                                    discount_on = product.getString("discount_on");
                                    if (!discount_on.equals("null")) {
                                        JSONArray discOn = new JSONArray(discount_on);
                                        for (int i = 0; i < discOn.length(); i++) {
                                            if (discOn.getJSONObject(i).getString("power").equals("NP")) {
                                                varian = "Y";

                                            }
                                            if (discOn.getJSONObject(i).getString("power").equals("A")) {
                                                varian = "Y";
                                            }
                                        }
                                        if (varian.equals("Y")) {
                                            Double D = Double.parseDouble(product.getString("price_without_power")) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                                            Double E = ((Double.parseDouble(product.getString("price_without_power"))) * Double.parseDouble(discount)) / 100;
                                            Double pri = D - E;
                                            String dis = noFormat(pri);
                                            totalPrice.setText(String.valueOf(dis) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            discountPrice.setText(noFormat(E) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            total_price.setText("1 " + getString(R.string.item_with) + String.valueOf(dis) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            totPric = String.valueOf(dis);
                                            mainPrice = String.valueOf(dis);
                                        } else {
                                            totalPrice.setText((noFormat(Double.parseDouble(product.getString("price_without_power")) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED")));

                                            total_price.setText("1 " + getString(R.string.item_with) + (noFormat(Double.parseDouble(product.getString("price_without_power")) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED")));

                                            totPric = product.getString("price_without_power");
                                            mainPrice = product.getString("price_without_power");
                                            discountPrice.setText("0 " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                        }
                                    } else {
                                        Double D = Double.parseDouble(product.getString("price_without_power"));
                                        Double E = ((Double.parseDouble(product.getString("price_without_power"))) * Double.parseDouble(discount)) / 100;
                                        Double pri = D - E;
                                        String dis = noFormat(pri);
                                        totalPrice.setText(String.valueOf(dis) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                        discountPrice.setText(noFormat(E) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                        total_price.setText("1 " + getString(R.string.item_with) + String.valueOf(dis) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                        totPric = String.valueOf(dis);
                                        mainPrice = String.valueOf(dis);
                                    }
                                }
                            } else {
                                totalPrice.setText((noFormat(Double.parseDouble(product.getString("price_without_power")) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED")));

                                total_price.setText("1 " + getString(R.string.item_with) + (noFormat(Double.parseDouble(product.getString("price_without_power")) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED")));

                                totPric = product.getString("price_without_power");
                                mainPrice = product.getString("price_without_power");
                                discountPrice.setText("0 " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                            }
                        } else {
                            totalPrice.setText((noFormat(Double.parseDouble(product.getString("price_without_power")) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED")));

                            total_price.setText("1 " + getString(R.string.item_with) + (noFormat(Double.parseDouble(product.getString("price_without_power")) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED")));

                            totPric = product.getString("price_without_power");
                            mainPrice = product.getString("price_without_power");
                            discountPrice.setText("0 " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                        }

                        totPric2 = product.getString("price_without_power");

                        simpleRatingBar.setRating(Float.parseFloat(product.getString("avg_review")));
                        if (product.getString("total_no_reviews").equals("null")) {
                            rating.setText("(0)");
                        } else {
                            rating.setText("(" + product.getString("total_no_reviews") + ")");
                        }

                        if (MethodClass.checkNull(product.getString("lens_material")).equals("")) {
                            replacement_lay.setVisibility(View.GONE);
                            replacement_line.setVisibility(View.GONE);
                        } else {
                            lensMate.setText(MethodClass.checkNull(product.getString("lens_material")));
                            replacement_lay.setVisibility(View.VISIBLE);
                            replacement_line.setVisibility(View.VISIBLE);
                        }

                        if (MethodClass.checkNull(product.getString("water_content")).equals("")) {
                            water_content_lay.setVisibility(View.GONE);
                            water_content_line.setVisibility(View.GONE);
                        } else {
                            wateContent.setText(product.getString("water_content") + "%");
                            water_content_lay.setVisibility(View.VISIBLE);
                            water_content_line.setVisibility(View.VISIBLE);
                        }

                        if (MethodClass.checkNull(product.getString("base_curve")).equals("")) {
                            base_curve_lay.setVisibility(View.GONE);
                            base_curve_line.setVisibility(View.GONE);
                        } else {
                            baseCurve.setText(MethodClass.checkNull(product.getString("base_curve")));
                            base_curve_lay.setVisibility(View.VISIBLE);
                            base_curve_line.setVisibility(View.VISIBLE);
                        }
                        if (MethodClass.checkNull(product.getString("diameter")).equals("")) {
                            diameter_lay.setVisibility(View.GONE);
                            diameter_line.setVisibility(View.GONE);
                        } else {
                            diameter.setText(MethodClass.checkNull(product.getString("diameter")));
                            diameter_lay.setVisibility(View.VISIBLE);
                            diameter_line.setVisibility(View.VISIBLE);
                        }


                        if (MethodClass.checkNull(product.getString("permeability")).equals("")) {
                            permAbility_lay.setVisibility(View.GONE);
                            permAbility_line.setVisibility(View.GONE);
                        } else {
                            permAbility.setText(MethodClass.checkNull(product.getString("permeability")));
                            permAbility_lay.setVisibility(View.VISIBLE);
                            permAbility_line.setVisibility(View.VISIBLE);
                        }

                        if (MethodClass.checkNull(product.getString("central_thickness")).equals("")) {
                            thickness_lay.setVisibility(View.GONE);
                            thickness_line.setVisibility(View.GONE);
                        } else {
                            thickness.setText(MethodClass.checkNull(product.getString("central_thickness")));
                            thickness_lay.setVisibility(View.VISIBLE);
                            thickness_line.setVisibility(View.VISIBLE);
                        }

                        if (MethodClass.checkNull(product.getString("transnissibility")).equals("")) {
                            transmisibility_lay.setVisibility(View.GONE);
                            transmisibility_line.setVisibility(View.GONE);
                        } else {
                            transmisibility.setText(product.getString("transnissibility") + "%");
                            transmisibility_lay.setVisibility(View.VISIBLE);
                            transmisibility_line.setVisibility(View.VISIBLE);
                        }
                        if (product.getJSONArray("wishlistfavourite").length() > 0) {
                            Picasso.get().load(R.drawable.fav1).fit().placeholder(R.drawable.fav1).error(R.drawable.fav1).into(favBtn);
                            favBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    removeFav(getIntent().getStringExtra("product_id"));
                                }
                            });

                        } else {
                            Picasso.get().load(R.drawable.fav).fit().placeholder(R.drawable.fav).error(R.drawable.fav).into(favBtn);
                            favBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getBoolean("is_logged_in", false)) {
                                        addFav(getIntent().getStringExtra("product_id"));
                                    } else {
                                        MethodClass.go_to_next_activity(ProductDetailsActivity.this, LoginActivity.class);
                                    }

                                }
                            });
                        }


                        if (PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getBoolean("is_logged_in", false)) {
                            if (product.has("product_user_review")) {
                                String product_user_review = product.getString("product_user_review");
                                if (product_user_review.equals(null) || product_user_review.equals("null")) {
                                    post_reiew_btn.setVisibility(View.VISIBLE);
                                } else {
                                    post_reiew_btn.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            post_reiew_btn.setVisibility(View.GONE);
                        }

                        if (resultResponse.getJSONObject("product").has("review")) {
                            JSONArray review = resultResponse.getJSONObject("product").getJSONArray("review");

                            if (review.length() == 0) {
                                review_lay.setVisibility(View.GONE);
                            } else {
                                review_lay.setVisibility(View.VISIBLE);
                            }
                            ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                            for (int i = 0; i < review.length(); i++) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put(PRODUCT_ID, review.getJSONObject(i).getString("product_id"));
                                hashMap.put(PRODUCT_IMAGE, review.getJSONObject(i).getString("image"));
                                hashMap.put(USER_NAME, review.getJSONObject(i).getJSONObject("customer_details").getString("name"));
                                hashMap.put(RATING_START, review.getJSONObject(i).getString("review_stars"));
                                hashMap.put(PRODUCT_DESC, review.getJSONObject(i).getString("review_text"));
                                hashMap.put(CRAETED_AT, review.getJSONObject(i).getString("created_at"));
                                hashMap.put(IS_POSTED, review.getJSONObject(i).getString("created_at"));
                                arrayList.add(hashMap);
                            }
                            ReviewAdapter reviewAdapter = new ReviewAdapter(ProductDetailsActivity.this, arrayList);
                            recy_view_review.setAdapter(reviewAdapter);
                            recy_view_review.setFocusable(false);
                            String no_review = resultResponse.getJSONObject("product").getJSONObject("product_details").getString("total_no_reviews");
                            all_review_text.setText(getString(R.string.read_all_review) + "(" + no_review + ")");
                            all_review_text.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ProductDetailsActivity.this, ReviewListActivity.class);
                                    intent.putExtra("product_id", getIntent().getStringExtra("product_id"));
                                    startActivity(intent);
                                }
                            });
                        }

                        JSONArray power = resultResponse.getJSONObject("product").getJSONArray("power");
                        agree_conditions = resultResponse.getJSONObject("product").getString("agree_conditions");
                        String monthly_category_text = resultResponse.getJSONObject("product").getString("monthly_category_text");
                        String monthly_category_image = resultResponse.getJSONObject("product").getString("monthly_category_image");
                        if (!monthly_category_text.equals("null") && !monthly_category_text.equals("")) {
                            mon_layout.setVisibility(View.VISIBLE);
                            mon_description.setText(monthly_category_text);
                            Picasso.get().load(SPLASH_IMAGE_URL + monthly_category_image).placeholder(R.drawable.loading).error(R.drawable.placeholder).into(mon_image);
                        } else {
                            mon_layout.setVisibility(View.GONE);
                        }
                        powerArrayList = new ArrayList<>();
                        powerArrayList.add(new MethodClass.StringWithTag(getString(R.string.select), "0"));
                        for (int i = 0; i < power.length(); i++) {
                            String id = power.getJSONObject(i).getString("id");
                            String eye_power = power.getJSONObject(i).getString("eye_power");
                            MethodClass.StringWithTag stringWithTag = new MethodClass.StringWithTag(eye_power, id);
                            powerArrayList.add(stringWithTag);
                        }

                        JSONArray sph = resultResponse.getJSONObject("product").getJSONArray("sph");
                        shp_array = new ArrayList<>();
                        shp_array.add(getString(R.string.select));
                        for (int i = 0; i < sph.length(); i++) {
                            shp_array.add(sph.getJSONObject(i).getString("value"));
                        }

                        JSONArray cyl = resultResponse.getJSONObject("product").getJSONArray("cyl");
                        cyl_array = new ArrayList<>();
                        cyl_array.add(getString(R.string.select));
                        for (int i = 0; i < cyl.length(); i++) {
                            cyl_array.add(cyl.getJSONObject(i).getString("value"));
                        }

                        JSONArray axis = resultResponse.getJSONObject("product").getJSONArray("axis");
                        ax_array = new ArrayList<>();
                        ax_array.add(getString(R.string.select));
                        for (int i = 0; i < axis.length(); i++) {
                            ax_array.add(axis.getJSONObject(i).getString("value"));
                        }
                        JSONArray base_curve = resultResponse.getJSONObject("product").getJSONArray("base_curve");
                        b_c_array = new ArrayList<>();
                        b_c_array.add(getString(R.string.select));
                        for (int i = 0; i < base_curve.length(); i++) {
                            b_c_array.add(base_curve.getJSONObject(i).getString("value"));
                        }
                        getPrice();
                        scrollView.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
                    MethodClass.error_alert(ProductDetailsActivity.this);
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
                MethodClass.hideProgressDialog(ProductDetailsActivity.this);
                if (error.toString().contains("ConnectException")) {
                    MethodClass.network_error_alert(ProductDetailsActivity.this);
                } else {
                    MethodClass.error_alert(ProductDetailsActivity.this);
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(ProductDetailsActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MethodClass.setCartCount(this);
        MethodClass.setMenu(this);
    }

    public void addFav(String eventId) {
        MethodClass.showProgressDialog(ProductDetailsActivity.this);
        String server_url = ProductDetailsActivity.this.getString(R.string.SERVER_URL) + "add-fav/" + eventId;
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        //params.put("event_id", eventId);

        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());
                try {
                    MethodClass.hideProgressDialog(ProductDetailsActivity.this);
                    JSONObject result_Object = MethodClass.get_result_from_webservice(ProductDetailsActivity.this, response);
                    if (result_Object != null) {
                        getProduct();
                        /*JSONObject MainMsg = result_Object.getJSONObject("message");
                        String message = MainMsg.getString("message");
                        String meaning = MainMsg.getString("meaning");
                        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(ProductDetailsActivity.this, DialogTypes.TYPE_SUCCESS)
                                .setTitle(message)
                                .setDescription(meaning)
                                .setPositiveText("Ok")
                                .setPositiveListener(new ClickListener() {
                                    @Override
                                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                                        lottieAlertDialog.dismiss();
                                    }
                                })
                                .build();
                        alertDialog.show();*/
                    }
                } catch (Exception e) {
                    MethodClass.hideProgressDialog(ProductDetailsActivity.this);
                    e.printStackTrace();
                    Log.e("error", e.getMessage());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(ProductDetailsActivity.this);
                Log.e("error", error.toString());
                if (error.toString().contains("AuthFailureError")) {
                    Toast.makeText(ProductDetailsActivity.this, "Authentication Failure.Please uninstall the app and reinstall it.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Something went wrong.....", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(ProductDetailsActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    public void removeFav(String eventId) {
        MethodClass.showProgressDialog(ProductDetailsActivity.this);
        String server_url = ProductDetailsActivity.this.getString(R.string.SERVER_URL) + "add-fav/" + eventId;
        Log.e("server_url", server_url);
        HashMap<String, String> params = new HashMap<String, String>();
        //params.put("event_id", eventId);

        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());
                try {
                    MethodClass.hideProgressDialog(ProductDetailsActivity.this);
                    JSONObject result_Object = MethodClass.get_result_from_webservice(ProductDetailsActivity.this, response);
                    if (result_Object != null) {
                        getProduct();
//                        JSONObject MainMsg = result_Object.getJSONObject("message");
//                        String message = MainMsg.getString("message");
//                        String meaning = MainMsg.getString("meaning");
//                        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(ProductDetailsActivity.this, DialogTypes.TYPE_SUCCESS)
//                                .setTitle(message)
//                                .setDescription(meaning)
//                                .setPositiveText("Ok")
//                                .setPositiveListener(new ClickListener() {
//                                    @Override
//                                    public void onClick(LottieAlertDialog lottieAlertDialog) {
//                                        lottieAlertDialog.dismiss();
//                                    }
//                                })
//                                .build();
//                        alertDialog.show();
                    }
                } catch (Exception e) {
                    MethodClass.hideProgressDialog(ProductDetailsActivity.this);
                    e.printStackTrace();
                    Log.e("error", e.getMessage());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MethodClass.hideProgressDialog(ProductDetailsActivity.this);
                Log.e("error", error.toString());
                if (error.toString().contains("AuthFailureError")) {
                    Toast.makeText(ProductDetailsActivity.this, "Authentication Failure.Please uninstall the app and reinstall it.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Something went wrong.....", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(ProductDetailsActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    public void getPrice() {
        MethodClass.showProgressDialog(ProductDetailsActivity.this);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = ProductDetailsActivity.this.getString(R.string.SERVER_URL) + "get-product-price";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("product_id", getIntent().getStringExtra("product_id"));
        params.put("device_id", android_id);
        params.put("quantity", String.valueOf(count));
        if (withPower.equals("Y")) {
            params.put("power_type_with_power", "Y");
            if (!left_power.equals("Select")) {
                params.put("left_eye", left_power);
            }
            if (!right_power.equals("Select")) {
                params.put("right_eye", right_power);
            }
        } else if (withPower.equals("A")) {
            params.put("power_type_with_astigmatism", "Y");
            if (!shp_str.equals("Select")) {
                params.put("left_sph", shp_str);
            }

            if (!cyl_str.equals("Select")) {
                params.put("left_cyl", cyl_str);
            }

            if (!ax_str.equals("Select")) {
                params.put("left_axis", ax_str);
            }

            if (!b_c_str.equals("Select")) {
                params.put("left_base_curve", b_c_str);
            }

            if (!r_shp_str.equals("Select")) {
                params.put("right_sph", r_shp_str);
            }

            if (!r_cyl_str.equals("Select")) {
                params.put("right_cyl", r_cyl_str);
            }

            if (!r_ax_str.equals("Select")) {
                params.put("right_axis", r_ax_str);
            }

            if (!r_b_c_str.equals("Select")) {
                params.put("right_base_curve", r_b_c_str);
            }
        } else {
            params.put("power_type_no_power", "Y");
        }

        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        Log.e("jsonObject", jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());
                try {
                    MethodClass.hideProgressDialog(ProductDetailsActivity.this);
                    JSONObject result_Object = MethodClass.get_result_from_webservice(ProductDetailsActivity.this, response);
                    if (result_Object != null) {
                        if (withPower.equals("Y")) {
                            JSONObject product = result_Object.getJSONObject("product");
                            String price = product.getString("price");
                            Double price_converted = Double.parseDouble(price) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                            optionPrice.setText(noFormat(price_converted) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                            optionName.setText(getString(R.string.power_price));
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date dCurrentDate = null;
                            Date toDate = null;
                            Date fromDate = null;
                            try {
                                fromDate = dateFormat.parse(discount_valid_from);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            try {
                                toDate = dateFormat.parse(discount_valid_till);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            try {
                                dCurrentDate = dateFormat.parse(dateFormat.format(new Date()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if ((dCurrentDate.equals(fromDate) || dCurrentDate.after(fromDate)) && (dCurrentDate.before(toDate) || dCurrentDate.equals(toDate))) {
                                if (!discount_min_order_total.equals("null") && Float.parseFloat(discount_min_order_total) > 0) {
                                    totalPrice.setText(noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(price)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                    total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(price) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                    totPric = noFormat(Double.parseDouble(mainPrice2) + Double.parseDouble(price));
                                    discountPrice.setText("0 " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                } else if (offer_type.equals("B") || offer_type.equals("D")) {
                                    if (discount_type.equals("F")) {
                                        String varian = "";
                                        if (!discount_on.equals("null")) {
                                            JSONArray discOn = new JSONArray(discount_on);
                                            for (int i = 0; i < discOn.length(); i++) {
                                                if (discOn.getJSONObject(i).getString("power").equals("WP")) {
                                                    varian = "Y";
                                                }
                                                if (discOn.getJSONObject(i).getString("power").equals("A")) {
                                                    varian = "Y";

                                                }

                                            }
                                            if (varian.equals("Y")) {
                                                Double D = (Double.parseDouble(mainPrice2) + Double.parseDouble(price)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                                                Double E = Double.parseDouble(discount);
                                                Double pri = D - E;
                                                totalPrice.setText(noFormat(pri) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                discountPrice.setText(noFormat(E) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat(pri * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                totPric = noFormat(pri);
                                            } else {
                                                totalPrice.setText(noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(price)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat(((Double.parseDouble(mainPrice2) + Double.parseDouble(price)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                totPric = noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(price)));
                                                discountPrice.setText("0 " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            }
                                        } else {
                                            Double D = (Double.parseDouble(mainPrice2) + Double.parseDouble(price)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                                            Double E = Double.parseDouble(discount);
                                            Double pri = D - E;
                                            discountPrice.setText(noFormat(E) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            totalPrice.setText(noFormat(pri) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat(pri * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            totPric = noFormat(pri);
                                        }
                                    }
                                    if (discount_type.equals("P")) {
                                        String varian = "";
                                        if (!discount_on.equals("null")) {
                                            JSONArray discOn = new JSONArray(discount_on);
                                            for (int i = 0; i < discOn.length(); i++) {

                                                if (discOn.getJSONObject(i).getString("power").equals("WP")) {
                                                    varian = "Y";
                                                }
                                                if (discOn.getJSONObject(i).getString("power").equals("A")) {
                                                    varian = "Y";

                                                }
                                            }
                                            if (varian.equals("Y")) {
                                                Double D = (Double.parseDouble(mainPrice2) + Double.parseDouble(price)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                                                Double E = ((Double.parseDouble(mainPrice2) + Double.parseDouble(price)) * Double.parseDouble(discount)) / 100;
                                                Double pri = D - E;
                                                totalPrice.setText(noFormat(pri) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                discountPrice.setText(noFormat(E) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat(pri * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                totPric = noFormat(pri);
                                            } else {
                                                totalPrice.setText(noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(price)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat(((Double.parseDouble(mainPrice2) + Double.parseDouble(price)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                totPric = noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(price)));
                                                discountPrice.setText("0 " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            }
                                        } else {
                                            Double D = (Double.parseDouble(mainPrice2) + Double.parseDouble(price)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                                            Double E = ((Double.parseDouble(mainPrice2) + Double.parseDouble(price)) * Double.parseDouble(discount)) / 100;
                                            Double pri = D - E;
                                            totalPrice.setText(noFormat(pri) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            discountPrice.setText(noFormat(E) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat(pri * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            totPric = noFormat(pri);
                                        }
                                    }
                                } else {
                                    totalPrice.setText(noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(price)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                    total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat(((Double.parseDouble(mainPrice2) + Double.parseDouble(price)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                    totPric = noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(price)));
                                    discountPrice.setText("0 " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                }
                            } else {
                                totalPrice.setText(noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(price)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat(((Double.parseDouble(mainPrice2) + Double.parseDouble(price)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                Log.e("chekcode", mainPrice2);
                                Log.e("chekcode2", price);
                                Log.e("chekcode3", String.valueOf(Double.parseDouble(mainPrice2) + Double.parseDouble(price)));
                                totPric = noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(price)));
                                discountPrice.setText("0 " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                            }

                            String discount = result_Object.getJSONObject("product").getString("discount");
                            discountPrice.setText(noFormat((Double.parseDouble(discount)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                            String total = result_Object.getJSONObject("product").getString("total");
                            totalPrice.setText(noFormat((Double.parseDouble(total)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));

                            left_power_tv.setText(MethodClass.checkNull(left_power));
                            right_power_tv.setText(MethodClass.checkNull(right_power));
                            diff_power_lay.setVisibility(View.VISIBLE);

                            withAsigma.setVisibility(View.GONE);
                            withpOWER.setVisibility(View.VISIBLE);
                            optionLay.setVisibility(View.VISIBLE);
                            withoutPower.setText(R.string.without);
                            withoutPower.setBackground(getDrawable(R.drawable.dark_grey_btn));
                            withpowerBtn.setText(R.string.right_with_power);
                            withpowerBtn.setBackground(getDrawable(R.drawable.light_grad));
                            withaignBtn.setText(getString(R.string.withAsig));
                            withaignBtn.setBackground(getDrawable(R.drawable.dark_grey_btn));
                        } else if (withPower.equals("A")) {
                            JSONObject product = result_Object.getJSONObject("product");
                            String price = product.getString("price");
                            Double price_converted = Double.parseDouble(price) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                            optionPrice.setText(noFormat(price_converted) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));

                            withaignBtn.setText(R.string.right_with_astigma);
                            withaignBtn.setBackground(getDrawable(R.drawable.light_grad));
                            withpowerBtn.setText(getString(R.string.WithP));
                            withpowerBtn.setBackground(getDrawable(R.drawable.dark_grey_btn));
                            withoutPower.setText(R.string.without);
                            withoutPower.setBackground(getDrawable(R.drawable.dark_grey_btn));

                            shp_tv.setText(MethodClass.checkNull(shp_str));
                            cyl_tv.setText(MethodClass.checkNull(cyl_str));
                            ax_tv.setText(MethodClass.checkNull(ax_str));
                            b_c_tv.setText(MethodClass.checkNull(b_c_str));

                            r_shp_tv.setText(MethodClass.checkNull(r_shp_str));
                            r_cyl_tv.setText(MethodClass.checkNull(r_cyl_str));
                            r_ax_tv.setText(MethodClass.checkNull(r_ax_str));
                            r_b_c_tv.setText(MethodClass.checkNull(r_b_c_str));

                            withAsigma.setVisibility(View.VISIBLE);
                            withpOWER.setVisibility(View.GONE);
                            optionLay.setVisibility(View.VISIBLE);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date dCurrentDate = null;
                            Date toDate = null;
                            Date fromDate = null;
                            try {
                                fromDate = dateFormat.parse(discount_valid_from);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            try {
                                toDate = dateFormat.parse(discount_valid_till);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            try {
                                dCurrentDate = dateFormat.parse(dateFormat.format(new Date()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if ((dCurrentDate.equals(fromDate) || dCurrentDate.after(fromDate)) && (dCurrentDate.before(toDate) || dCurrentDate.equals(toDate))) {
                                if (!discount_min_order_total.equals("null") && Float.parseFloat(discount_min_order_total) > 0) {
                                    totalPrice.setText(noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                    total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat(((Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                    totPric = noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)));
                                    discountPrice.setText("0 " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                } else if (offer_type.equals("B") || offer_type.equals("D")) {
                                    if (discount_type.equals("F")) {
                                        String varian = "";
                                        if (!discount_on.equals("null")) {
                                            JSONArray discOn = new JSONArray(discount_on);
                                            for (int i = 0; i < discOn.length(); i++) {
                                                if (discOn.getJSONObject(i).getString("power").equals("WA")) {
                                                    varian = "Y";

                                                }
                                                if (discOn.getJSONObject(i).getString("power").equals("A")) {
                                                    varian = "Y";
                                                }

                                            }
                                            if (varian.equals("Y")) {
                                                Double D = (Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                                                Double E = Double.parseDouble(discount);
                                                Double pri = D - E;
                                                totalPrice.setText(noFormat(pri) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                discountPrice.setText(noFormat(E) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat(pri * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                totPric = noFormat(pri);
                                            } else {
                                                totalPrice.setText(noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat(((Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                totPric = noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)));
                                                discountPrice.setText("0 " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            }
                                        } else {
                                            Double D = (Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                                            Double E = Double.parseDouble(discount);
                                            Double pri = D - E;
                                            discountPrice.setText(noFormat(E) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            totalPrice.setText(noFormat(pri) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat(pri * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            totPric = noFormat(pri);
                                        }
                                    }
                                    if (discount_type.equals("P")) {
                                        String varian = "";
                                        if (!discount_on.equals("null")) {
                                            JSONArray discOn = new JSONArray(discount_on);
                                            for (int i = 0; i < discOn.length(); i++) {
                                                if (discOn.getJSONObject(i).getString("power").equals("WA")) {
                                                    varian = "Y";
                                                }
                                                if (discOn.getJSONObject(i).getString("power").equals("A")) {
                                                    varian = "Y";

                                                }
                                            }
                                            if (varian.equals("Y")) {
                                                Double D = (Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                                                Double E = ((Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)) * Double.parseDouble(discount)) / 100;
                                                Double pri = D - E;
                                                totalPrice.setText(noFormat(pri) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                discountPrice.setText(noFormat(E) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat(pri * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                totPric = noFormat(pri);
                                            } else {
                                                totalPrice.setText(noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat(((Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                                totPric = noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)));
                                                discountPrice.setText("0 " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            }
                                        } else {
                                            Double D = (Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""));
                                            Double E = ((Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)) * Double.parseDouble(discount)) / 100;
                                            Double pri = D - E;
                                            totalPrice.setText(noFormat(pri) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            discountPrice.setText(noFormat(E) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat(pri * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                            totPric = noFormat(pri);
                                        }
                                    }
                                } else {
                                    Log.e("PRICE", mainPrice2 + "-------" + aisgmaPrice);
                                    totalPrice.setText(noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                    total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat(((Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                    totPric = noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)));
                                    discountPrice.setText("0 " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                }
                            } else {
                                totalPrice.setText(noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                total_price.setText(String.valueOf(count) + getString(R.string.item_with) + noFormat(((Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) * count) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                                totPric = noFormat((Double.parseDouble(mainPrice2) + Double.parseDouble(aisgmaPrice)));
                                discountPrice.setText("0 " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));

                            }
                            String discount = result_Object.getJSONObject("product").getString("discount");
                            discountPrice.setText(noFormat((Double.parseDouble(discount)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                            String total = result_Object.getJSONObject("product").getString("total");
                            totalPrice.setText(noFormat((Double.parseDouble(total)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));

                        } else {
                            String discount = result_Object.getJSONObject("product").getString("discount");
                            discountPrice.setText(noFormat((Double.parseDouble(discount)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));
                            String total = result_Object.getJSONObject("product").getString("total");
                            totalPrice.setText(noFormat((Double.parseDouble(total)) * Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("conv_factor", ""))) + " " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("currency_code", "AED"));

                        }
                    }
                } catch (JSONException e) {
                    FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
                    MethodClass.hideProgressDialog(ProductDetailsActivity.this);
                    e.printStackTrace();
                    Log.e("error", e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
                MethodClass.hideProgressDialog(ProductDetailsActivity.this);
                Log.e("error", error.toString());
                if (error.toString().contains("AuthFailureError")) {
                    Toast.makeText(ProductDetailsActivity.this, "Authentication Failure.Please uninstall the app and reinstall it.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Something went wrong.....", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(ProductDetailsActivity.this).addToRequestQueue(jsonObjectRequest);
    }


    public void add_to_Cart() {
        MethodClass.showProgressDialog(ProductDetailsActivity.this);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String server_url = ProductDetailsActivity.this.getString(R.string.SERVER_URL) + "add-to-cart";
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("product_id", getIntent().getStringExtra("product_id"));
        params.put("device_id", android_id);
        params.put("quantity", String.valueOf(count));
        if (withPower.equals("Y")) {
            params.put("power_type_with_power", "Y");
            if (!left_power.equals("Select")) {
                params.put("left_eye", left_power);
            }
            if (!right_power.equals("Select")) {
                params.put("right_eye", right_power);
            }
        } else if (withPower.equals("A")) {
            params.put("power_type_with_astigmatism", "Y");
            if (!shp_str.equals("Select")) {
                params.put("left_sph", shp_str);
            }

            if (!cyl_str.equals("Select")) {
                params.put("left_cyl", cyl_str);
            }

            if (!ax_str.equals("Select")) {
                params.put("left_axis", ax_str);
            }

            if (!b_c_str.equals("Select")) {
                params.put("left_base_curve", b_c_str);
            }

            if (!r_shp_str.equals("Select")) {
                params.put("right_sph", r_shp_str);
            }

            if (!r_cyl_str.equals("Select")) {
                params.put("right_cyl", r_cyl_str);
            }

            if (!r_ax_str.equals("Select")) {
                params.put("right_axis", r_ax_str);
            }

            if (!r_b_c_str.equals("Select")) {
                params.put("right_base_curve", r_b_c_str);
            }
        } else {
            params.put("power_type_no_power", "Y");
        }


        Log.e("ADD TO CART", new JSONObject(params).toString());

        JSONObject jsonObject = MethodClass.Json_rpc_format(params);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("resp", response.toString());
                try {
                    MethodClass.hideProgressDialog(ProductDetailsActivity.this);
                    JSONObject result_Object = MethodClass.get_result_from_webservice(ProductDetailsActivity.this, response);
                    if (result_Object != null) {
                        String total_cart_item = result_Object.getString("total_cart_item");
                        PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).edit().putString("cart_count", total_cart_item).commit();
                        MethodClass.setCartCount(ProductDetailsActivity.this);

                        JSONObject MainMsg = result_Object.getJSONObject("message");
                        String message = MainMsg.getString("message");
                        String meaning = MainMsg.getString("meaning");
                        LottieAlertDialog alertDialog = new LottieAlertDialog.Builder(ProductDetailsActivity.this, DialogTypes.TYPE_SUCCESS)
                                .setTitle(message)
                                .setDescription(meaning)
                                .setPositiveText(getString(R.string.conti))
                                .setNegativeText(getString(R.string.gotoCart))
                                .setPositiveListener(new ClickListener() {
                                    @Override
                                    public void onClick(LottieAlertDialog lottieAlertDialog) {
                                        lottieAlertDialog.dismiss();
                                    }
                                })
                                .setNegativeListener(new ClickListener() {
                                    @Override
                                    public void onClick(@NotNull LottieAlertDialog lottieAlertDialog) {
                                        lottieAlertDialog.dismiss();
                                        Intent I = new Intent(ProductDetailsActivity.this, ShoppingCartActivity.class);
                                        startActivity(I);
                                    }
                                })
                                .build();
                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
                    MethodClass.hideProgressDialog(ProductDetailsActivity.this);
                    e.printStackTrace();
                    Log.e("error", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
                MethodClass.hideProgressDialog(ProductDetailsActivity.this);
                Log.e("error", error.toString());
                if (error.toString().contains("AuthFailureError")) {
                    Toast.makeText(ProductDetailsActivity.this, "Authentication Failure.Please uninstall the app and reinstall it.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Something went wrong.....", Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("X-localization", PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("lang", "en"));
                if (PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getBoolean("is_logged_in", false)) {
                    headers.put("Authorization", "Bearer " + PreferenceManager.getDefaultSharedPreferences(ProductDetailsActivity.this).getString("token", ""));
                }
                Log.e("getHeaders: ", headers.toString());
                return headers;
            }
        };
        MySingleton.getInstance(ProductDetailsActivity.this).addToRequestQueue(jsonObjectRequest);
    }


    public void search(View view) {
        Intent I = new Intent(this, SearchActivity.class);
        startActivity(I);
    }

    public void back(View view) {
        onBackPressed();
    }

    public void postReview(View view) {
        Intent intent = new Intent(ProductDetailsActivity.this, PostReviewActivity.class);
        intent.putExtra("product_id", getIntent().getStringExtra("product_id"));
        startActivity(intent);
    }

    public void shopping_cart(View view) {
        Intent I = new Intent(this, ShoppingCartActivity.class);
        startActivity(I);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(MethodClass.updateBaseContextLocale(base));
    }
}