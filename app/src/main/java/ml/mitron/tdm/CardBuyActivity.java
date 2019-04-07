package ml.mitron.tdm;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

public class CardBuyActivity extends AppCompatActivity {

    Integer currentBuyingStep;

    TDMCard.CARD_TYPE cardType;

    CardBuyDataFragment cardBuyDataFragment;
    CardBuySelectFragment cardBuySelectFragment;
    CardBuyConfirmFragment cardBuyConfirmFragment;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_buy);

        currentBuyingStep = 0;

        cardBuyDataFragment = new CardBuyDataFragment();
        cardBuySelectFragment = new CardBuySelectFragment();
        cardBuyConfirmFragment = new CardBuyConfirmFragment();

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().add(R.id.fragment, cardBuySelectFragment).commit();

        ((Button) findViewById(R.id.button_card_buy_stepper_aceptar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentBuyingStep++;
                updateBuyingStep(true);
            }
        });

        ((Button) findViewById(R.id.button_card_buy_stepper_cancelar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardBuyActivity.this.onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        currentBuyingStep--;
        updateBuyingStep(false);
        super.onBackPressed();
    }

    void updateBuyingStep(boolean isForwardStep) {
        switch (currentBuyingStep) {
            case 0:
                if (isForwardStep) {
                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.fragment, cardBuySelectFragment).addToBackStack(null).commit();
                }
                setStepOnStepper(0);
                break;
            case 1:
                switch (cardBuySelectFragment.viewPager.getCurrentItem()) {
                    case 0:
                        cardType = TDMCard.CARD_TYPE.ELEMENT;
                        break;
                    case 1:
                        cardType = TDMCard.CARD_TYPE.INFINITY;
                        break;
                }
                if (isForwardStep) {
                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.fragment, cardBuyDataFragment).addToBackStack(null).commit();
                }
                setStepOnStepper(1);
                break;
            case 2:
                cardBuyConfirmFragment.setCardType(cardType);
                if (isForwardStep) {
                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.fragment, cardBuyConfirmFragment).addToBackStack(null).commit();
                }
                setStepOnStepper(2);
                break;
            case 3:
                finish();
        }
    }

    void setStepOnStepper(Integer accentStepper) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator accentAnimator = null;
        ObjectAnimator grisAnimator1 = null;
        ObjectAnimator grisAnimator2 = null;
        ObjectAnimator accentTextAnimator = null;
        ObjectAnimator grisTextAnimator1 = null;
        ObjectAnimator grisTextAnimator2 = null;
        String propertyNameBackgroundColor = "backgroundColor";
        String propertyNameTextColor = "textColor";
        int accentColor = ContextCompat.getColor(this, R.color.colorAccent);
        int darkAccentColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        int grisColor = ContextCompat.getColor(this, R.color.gris);
        int whiteColor = ContextCompat.getColor(this, R.color.blanco);
        int grisTextColor = ContextCompat.getColor(this, R.color.negro);
        switch (accentStepper) {
            default:
            case 0:
                accentAnimator = ObjectAnimator.ofArgb(findViewById(R.id.frameLayout_buy_step_1), propertyNameBackgroundColor, ((ColorDrawable) ((FrameLayout) findViewById(R.id.frameLayout_buy_step_1)).getBackground()).getColor(), accentColor);
                grisAnimator1 = ObjectAnimator.ofArgb(findViewById(R.id.frameLayout_buy_step_2), propertyNameBackgroundColor, ((ColorDrawable) ((FrameLayout) findViewById(R.id.frameLayout_buy_step_2)).getBackground()).getColor(), grisColor);
                grisAnimator2 = ObjectAnimator.ofArgb(findViewById(R.id.frameLayout_buy_step_3), propertyNameBackgroundColor, ((ColorDrawable) ((FrameLayout) findViewById(R.id.frameLayout_buy_step_3)).getBackground()).getColor(), grisColor);
                accentTextAnimator = ObjectAnimator.ofArgb(findViewById(R.id.textView_frameLayout_buy_step_1), propertyNameTextColor, ((TextView) findViewById(R.id.textView_frameLayout_buy_step_1)).getCurrentTextColor(), whiteColor);
                grisTextAnimator1 = ObjectAnimator.ofArgb(findViewById(R.id.textView_frameLayout_buy_step_2), propertyNameTextColor, ((TextView) findViewById(R.id.textView_frameLayout_buy_step_2)).getCurrentTextColor(), grisTextColor);
                grisTextAnimator2 = ObjectAnimator.ofArgb(findViewById(R.id.textView_frameLayout_buy_step_3), propertyNameTextColor, ((TextView) findViewById(R.id.textView_frameLayout_buy_step_3)).getCurrentTextColor(), grisTextColor);
                break;
            case 1:
                accentAnimator = ObjectAnimator.ofArgb(findViewById(R.id.frameLayout_buy_step_2), propertyNameBackgroundColor, ((ColorDrawable) ((FrameLayout) findViewById(R.id.frameLayout_buy_step_2)).getBackground()).getColor(), accentColor);
                grisAnimator1 = ObjectAnimator.ofArgb(findViewById(R.id.frameLayout_buy_step_1), propertyNameBackgroundColor, ((ColorDrawable) ((FrameLayout) findViewById(R.id.frameLayout_buy_step_1)).getBackground()).getColor(), darkAccentColor);
                grisAnimator2 = ObjectAnimator.ofArgb(findViewById(R.id.frameLayout_buy_step_3), propertyNameBackgroundColor, ((ColorDrawable) ((FrameLayout) findViewById(R.id.frameLayout_buy_step_3)).getBackground()).getColor(), grisColor);
                accentTextAnimator = ObjectAnimator.ofArgb(findViewById(R.id.textView_frameLayout_buy_step_2), propertyNameTextColor, ((TextView) findViewById(R.id.textView_frameLayout_buy_step_2)).getCurrentTextColor(), whiteColor);
                grisTextAnimator1 = ObjectAnimator.ofArgb(findViewById(R.id.textView_frameLayout_buy_step_1), propertyNameTextColor, ((TextView) findViewById(R.id.textView_frameLayout_buy_step_1)).getCurrentTextColor(), whiteColor);
                grisTextAnimator2 = ObjectAnimator.ofArgb(findViewById(R.id.textView_frameLayout_buy_step_3), propertyNameTextColor, ((TextView) findViewById(R.id.textView_frameLayout_buy_step_3)).getCurrentTextColor(), grisTextColor);
                break;
            case 2:
                accentAnimator = ObjectAnimator.ofArgb(findViewById(R.id.frameLayout_buy_step_3), propertyNameBackgroundColor, ((ColorDrawable) ((FrameLayout) findViewById(R.id.frameLayout_buy_step_3)).getBackground()).getColor(), accentColor);
                grisAnimator1 = ObjectAnimator.ofArgb(findViewById(R.id.frameLayout_buy_step_2), propertyNameBackgroundColor, ((ColorDrawable) ((FrameLayout) findViewById(R.id.frameLayout_buy_step_2)).getBackground()).getColor(), darkAccentColor);
                grisAnimator2 = ObjectAnimator.ofArgb(findViewById(R.id.frameLayout_buy_step_1), propertyNameBackgroundColor, ((ColorDrawable) ((FrameLayout) findViewById(R.id.frameLayout_buy_step_1)).getBackground()).getColor(), darkAccentColor);
                accentTextAnimator = ObjectAnimator.ofArgb(findViewById(R.id.textView_frameLayout_buy_step_3), propertyNameTextColor, ((TextView) findViewById(R.id.textView_frameLayout_buy_step_3)).getCurrentTextColor(), whiteColor);
                grisTextAnimator1 = ObjectAnimator.ofArgb(findViewById(R.id.textView_frameLayout_buy_step_2), propertyNameTextColor, ((TextView) findViewById(R.id.textView_frameLayout_buy_step_2)).getCurrentTextColor(), whiteColor);
                grisTextAnimator2 = ObjectAnimator.ofArgb(findViewById(R.id.textView_frameLayout_buy_step_1), propertyNameTextColor, ((TextView) findViewById(R.id.textView_frameLayout_buy_step_1)).getCurrentTextColor(), whiteColor);
                break;
        }


        animatorSet.playTogether(grisAnimator1, grisAnimator2, accentAnimator, grisTextAnimator1, grisTextAnimator2, accentTextAnimator);
        animatorSet.setDuration(1000);
        animatorSet.start();
    }
}
