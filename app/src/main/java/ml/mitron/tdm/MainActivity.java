package ml.mitron.tdm;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {

    static final int SEARCH_REQUEST_CODE = 1;


    Context contexto;
    SQLDBExtractor extractor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        contexto = getApplicationContext();

        setContentView(R.layout.activity_main);

        FirebaseDBExtractor firebaseDBExtractor = new FirebaseDBExtractor();


        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.card_reel_FrameLayout);
        getSupportFragmentManager().beginTransaction().add(frameLayout.getId(), new CardReelFragment()).commit();



        //TextView valueTV = new TextView(this);

        TextView searchOrigen = (TextView) findViewById(R.id.searchOrigen);
        TextView searchDestino = (TextView) findViewById(R.id.searchDestino);

        ConstraintLayout seleccionDestino = (ConstraintLayout) findViewById(R.id.seleccionDestino);
        seleccionDestino.setVisibility(View.GONE);

        //SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //searchOrigen.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchDestino.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchOrigen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SearchableActivity.class);
                intent.putExtra("seleccionInicio", true);
                TextView searchField = (TextView) v;
                v.setTransitionName("searchField");
                intent.putExtra("searchValue", searchField.getText());
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext(), searchField, "searchField");
                ((Activity) v.getContext()).startActivityForResult(intent, SEARCH_REQUEST_CODE, options.toBundle());
            }
        });

        searchDestino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SearchableActivity.class);
                intent.putExtra("seleccionInicio", false);
                TextView searchField = (TextView) v;
                v.setTransitionName("searchField");
                intent.putExtra("searchValue", searchField.getText());
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext(), searchField, "searchField");
                ((Activity) v.getContext()).startActivityForResult(intent, SEARCH_REQUEST_CODE, options.toBundle());
            }
        });


        Button botonBusqueda = (Button) findViewById(R.id.botonBusqueda);
        botonBusqueda.setVisibility(View.GONE);

        botonBusqueda.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (extractor == null) {
                    extractor = SQLDBExtractor.getExtractor(contexto);
                }

                if (!extractor.isOpen()) {
                    extractor.OpenDB();
                }

                Integer inicio = Integer.valueOf(0);
                Integer destino = Integer.valueOf(0);
                final TextView searchOrigen = (TextView) findViewById(R.id.searchOrigen);
                final TextView searchDestino = (TextView) findViewById(R.id.searchDestino);

                for (int i = extractor.getEstacionCount(); i > 0; i--) {
                    if (searchOrigen.getText().toString().equals(extractor.getEstacion((Integer) i).getNombre())) {
                        inicio = i;
                        break;
                    }
                }

                for (int i = extractor.getEstacionCount(); i > 0; i--) {
                    if (searchDestino.getText().toString().equals(extractor.getEstacion((Integer) i).getNombre())) {
                        destino = i;
                        break;
                    }
                }

                /*

                REVISAR

                POR QUE NO FUNCIONA

                Pair<View,String> pair1 = Pair.create(findViewById(R.id.searchOrigen),findViewById(R.id.searchOrigen).getTransitionName());

                Y EN CAMBIO

                Pair<View,String> pair1 = Pair.create(findViewById(R.id.searchOrigen),"estacionOrigen");

                SI QUE FUNCIONA?

                 */

                Pair<View, String> pair1 = Pair.create(findViewById(R.id.searchOrigen), "estacionOrigen");
                //Pair<View,String> pair1 = Pair.create(findViewById(R.id.searchOrigen),findViewById(R.id.searchOrigen).getTransitionName());
                Pair<View, String> pair2 = Pair.create(findViewById(R.id.searchDestino), "estacionDestino");
                //Pair<View,String> pair2 = Pair.create(findViewById(R.id.searchDestino),findViewById(R.id.searchDestino).getTransitionName());
                Pair<View, String> pair3 = Pair.create(findViewById(R.id.icono_estacionOrigen), findViewById(R.id.icono_estacionOrigen).getTransitionName());
                Pair<View, String> pair4 = Pair.create(findViewById(R.id.icono_estacionDestino), findViewById(R.id.icono_estacionDestino).getTransitionName());

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pair1, pair2, pair3, pair4);

                Intent intent = new Intent(contexto, RutaActivity.class);

                intent.putExtra("inicio", inicio);
                intent.putExtra("destino", destino);

                startActivity(intent, options.toBundle());
            }
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton_myTDM);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
        floatingActionButton.getDrawable().mutate().setTint(getResources().getColor(R.color.blanco));

        /*myHandler = new Handler();

        Runnable noriaChange = new Runnable() {
            @Override
            public void run() {

                TextView nombreEstacionNoria = (TextView) findViewById(R.id.nombreEstacionNoria);
                ObjectAnimator animatorNoria = ObjectAnimator.ofFloat(nombreEstacionNoria, View.ALPHA, 1f, 0f);
                animatorNoria.setDuration(1000);
                animatorNoria.start();

                animatorNoria.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(final Animator animation) {
                        super.onAnimationEnd(animation);

                        myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (extractor == null) {
                                    extractor = SQLDBExtractor.getExtractor(contexto);
                                }

                                if (!extractor.isOpen()) {
                                    extractor.OpenDB();
                                }

                                Integer idNoria;
                                Random noriaRandom = new Random();
                                idNoria = new Integer(noriaRandom.nextInt(extractor.getEstacionCount()) + 1);
                                TextView nombreEstacionNoria = (TextView) findViewById(R.id.nombreEstacionNoria);
                                nombreEstacionNoria.setText(extractor.getEstacion(idNoria).getNombre());

                                ObjectAnimator animatorNoria = ObjectAnimator.ofFloat(nombreEstacionNoria, View.ALPHA, 0f, 1f);
                                animatorNoria.setDuration(1000);
                                animatorNoria.start();
                            }
                        });

                    }
                });

                myHandler.postDelayed(this, 3000);
            }
        };

        myHandler.postDelayed(noriaChange, 2000);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                ViewGroup group = (ViewGroup) findViewById(R.id.cardSeleccion);
                Transition revealTransition = new AutoTransition();
                //revealTransition.setDuration(1500);
                revealTransition.setInterpolator(new BounceInterpolator());

                if (data.getBooleanExtra("seleccionInicio", false)) {
                    ((TextView) findViewById(R.id.searchOrigen)).setText(data.getStringExtra("seleccion"));
                    if (findViewById(R.id.seleccionDestino).getVisibility() == View.GONE) {

                        TransitionManager.beginDelayedTransition(group, revealTransition);
                        findViewById(R.id.seleccionDestino).setVisibility(View.VISIBLE);
                    }
                } else {
                    ((TextView) findViewById(R.id.searchDestino)).setText(data.getStringExtra("seleccion"));
                    if (findViewById(R.id.botonBusqueda).getVisibility() == View.GONE) {
                        TransitionManager.beginDelayedTransition(group, revealTransition);
                        findViewById(R.id.botonBusqueda).setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle bundle = new Bundle();
        bundle.putInt("visibility_seleccionDestino", findViewById(R.id.seleccionDestino).getVisibility());
        bundle.putInt("visibility_botonBusqueda", findViewById(R.id.botonBusqueda).getVisibility());

        bundle.putCharSequence("texto_searchOrigen", ((TextView) findViewById(R.id.searchOrigen)).getText());
        bundle.putCharSequence("texto_searchDestino", ((TextView) findViewById(R.id.searchDestino)).getText());

        outState.putAll(bundle);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.getInt("visibility_seleccionDestino", View.GONE) == View.VISIBLE) {
            findViewById(R.id.seleccionDestino).setVisibility(View.VISIBLE);
        }
        if (savedInstanceState.getInt("visibility_botonBusqueda", View.GONE) == View.VISIBLE) {
            findViewById(R.id.botonBusqueda).setVisibility(View.VISIBLE);
        }

        ((TextView) findViewById(R.id.searchOrigen)).setText(savedInstanceState.getCharSequence("texto_searchOrigen"));
        ((TextView) findViewById(R.id.searchDestino)).setText(savedInstanceState.getCharSequence("texto_searchDestino"));
    }
}
