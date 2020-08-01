package ml.mitron.tdm;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.TestOnly;

public class CardBuyActivity extends AppCompatActivity {

    Integer currentBuyingStep;

    TDMCard.CARD_TYPE cardType;

    int cardColor;

    CardBuyDataFragment cardBuyDataFragment;
    CardBuySelectFragment cardBuySelectFragment;
    CardBuyConfirmFragment cardBuyConfirmFragment;
    CardBuyDataColorSelectFragment cardBuyDataColorSelectFragment;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_buy);

        currentBuyingStep = 0;

        cardBuyDataFragment = new CardBuyDataFragment();
        cardBuySelectFragment = new CardBuySelectFragment();
        cardBuyConfirmFragment = new CardBuyConfirmFragment();
        cardBuyDataColorSelectFragment = new CardBuyDataColorSelectFragment();

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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //fragmentManager.popBackStack(savedInstanceState.getString("fragment_tag"), 0);
        currentBuyingStep = savedInstanceState.getInt("currentBuyingStep", 0);
        updateBuyingStep(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //outState.putString("fragment_tag", fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1).getTag());
        super.onSaveInstanceState(outState);
        outState.putInt("currentBuyingStep", currentBuyingStep);
    }

    void updateBuyingStep(boolean isForwardStep) {
        switch (currentBuyingStep) {
            case 0:
                if (isForwardStep) {
                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.fragment, cardBuySelectFragment).addToBackStack(cardBuySelectFragment.getTag()).commit();
                } /*else {
                    fragmentManager.popBackStack(cardBuySelectFragment.getTag(), 0);
                    fragmentManager.beginTransaction().replace(R.id.fragment, cardBuySelectFragment).commit();
                }*/
                setStepOnStepper(0);
                break;
            case 1:
                switch (cardBuySelectFragment.viewPager.getCurrentItem()) {
                    case 0:
                        cardType = TDMCard.CARD_TYPE.INFINITY;
                        break;
                    case 1:
                        cardType = TDMCard.CARD_TYPE.ELEMENT;
                        break;
                }
                if (isForwardStep) {
                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.fragment, cardBuyDataFragment).addToBackStack(cardBuyDataFragment.getTag()).commit();
                } /*else {
                    fragmentManager.popBackStack(cardBuyDataFragment.getTag(), 0);
                    fragmentManager.beginTransaction().replace(R.id.fragment, cardBuyDataFragment).commit();
                }*/
                setStepOnStepper(1);
                break;
            case 2:
                if (isForwardStep) {
                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.fragment, cardBuyDataColorSelectFragment).addToBackStack(cardBuyDataColorSelectFragment.getTag()).commit();
                } /*else {
                    fragmentManager.popBackStack(cardBuyDataColorSelectFragment.getTag(), 0);
                    fragmentManager.beginTransaction().replace(R.id.fragment, cardBuyDataColorSelectFragment).commit();
                }*/
                setStepOnStepper(1);
                break;
            case 3:
                cardColor = cardBuyDataColorSelectFragment.selectedColor;

                Toast.makeText(getBaseContext(), String.valueOf(cardColor), Toast.LENGTH_SHORT).show();

                cardBuyConfirmFragment.setCardType(cardType);
                if (isForwardStep) {
                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.fragment, cardBuyConfirmFragment).addToBackStack(cardBuyConfirmFragment.getTag()).commit();
                } /*else {
                    fragmentManager.popBackStack(cardBuyConfirmFragment.getTag(), 0);
                    fragmentManager.beginTransaction().replace(R.id.fragment, cardBuyConfirmFragment).commit();
                }*/
                setStepOnStepper(2);
                break;
            case 4:
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
        animatorSet.setDuration(500);
        animatorSet.start();
    }
}
