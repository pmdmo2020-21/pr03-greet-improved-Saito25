package es.iessaladillo.pedrojoya.pr02_greetimproved;

import static es.iessaladillo.pedrojoya.pr02_greetimproved.utils.SoftInputUtils.*;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import es.iessaladillo.pedrojoya.pr02_greetimproved.databinding.MainActivityBinding;

/**
 * Clase principal de la aplicación.
 * <p>
 * Muestra una interfaz de usuario en la que se puede introducir nombre y apellidos, para
 * obtener un saludo; el cual puede ser educado o informal. Además, permite cambiar a la
 * versión PRO de la aplicación.
 *
 * @author Manuel
 * @version 2
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Clase autogenerada. Nos permitirá acceder a las vistas que compongan el layout actual.
     */
    MainActivityBinding binding;

    /**
     * Contador del número de veces que se ha saludado.
     * Se reinicia al pasar a premium.
     */
    private int counter = 0;

    /**
     * Intento de constante. Delimita el número máximo de veces que se puede hacer uso
     * del botón saludo siendo un usuario no premium.
     * <p>
     * Se ha optado por poner el número como recurso de entero, dado que es un valor
     * que no debería cambiar a lo largo de la aplicación.
     */
    private int maxIteration; // No he podido declararlo como final.

    /**
     * Booleano que identifica si el saludo debe ser informal o formal.
     */
    private boolean isPolitely;

    /**
     * Booleano que identifica si el usuario tiene privilegios o no.
     */
    private boolean isPremium;

    /**
     * Booleano que determina el prefijo que se le pondrá al usuario si el saludo es
     * educado.
     */
    private String greetPrefix;

    /**
     * Determina el número máximo de caracteres para los EditTexts
     */
    private int maxCharsForEditext;

    /**
     * Determina el número restantes de caracteres para el nombre
     */
    private int nameRemainingChars;

    /**
     * Determina el número restantes de caracteres para el apellido
     */
    private int surnameRemainingChars;

    /**
     * Determina la acción cuando el usuario modifica el EditText del nombre
     */
    TextWatcher nameTextWatcher;

    /**
     * Determina la acción cuando el usuario modifica el EditText del apellido
     */
    TextWatcher lastNameTextWatcher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
            Cuando se llame al método, obtendremos el árbol de vistas para la interfaz
            y todas las vistas posibles para la misma, que se almacenará en binding.

            Además, se llamarán a diferentes funciones de configuración de la aplicación.
         */
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupFields(); // configura los campos.
        setupViews(); // configura las vistas
        actualizarNumeroSaludos(); // inicializa el mensaje de la cantidad de saludos. Por defecto 0.
        setupInitialState(); // Configuraciones iniciales del foco de la aplicación

    }

    /**
     * Inicializa los atributos de la clase que sean necesarios inicializar para el correcto
     * funcionamiento de la misma.
     */
    private void setupFields() {

        // Inicio de variables de campo normales.
        maxCharsForEditext = getResources().getInteger(R.integer.main_integer_maxChars);
        nameRemainingChars = getResources().getInteger(R.integer.main_integer_maxChars);
        surnameRemainingChars = getResources().getInteger(R.integer.main_integer_maxChars);
        maxIteration = getResources().getInteger(R.integer.main_integer_maxGreet);
        greetPrefix = getString(R.string.rb_main_mr);

        // Inicio de variable de campos con clases.
        nameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkValueOfForm(binding.etxtMainName);
            }
        };

        lastNameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkValueOfForm(binding.etxtMainLastName);
            }
        };
    }

    /**
     * Inicializa la configuración inicial de las vistas.
     *
     * Se ha definido el comportamiento de las vistas cuando el usuario interactúe con ellas.
     */
    private void setupViews() {
        // Configuración inicial para la cadena que muestra el número de caracteres restantes del nombre
        binding.txtMainNameChars.setText(getResources()
                .getQuantityString(R.plurals.txt_main_name_times, counter, surnameRemainingChars));

        // Configuración inicial para la cadena que muestra el número de caracteres restantes del apellido
        binding.txtMainLastNameChars.setText(getResources()
                .getQuantityString(R.plurals.txt_main_name_times, counter, nameRemainingChars));


        // Acción cuando el usuario presione el botón.
        binding.btnMainGreet.setOnClickListener(l -> modificarNumeroDePulsaciones());

        // Acción cuando el usuario marque/desmarque la opción educado.
        binding.chkMainEducado.setOnCheckedChangeListener(this::actualizarSaludo);

        // Acción cuando el usuario marque/desmarque la opción premium.
        binding.swtMainPremium.setOnCheckedChangeListener(this::cambiarPremium);

        // Acción cuando el usuario cambie de opción en el RadioGroup.
        binding.rgMainSelectPrefix.setOnCheckedChangeListener(this::selectGreetPrefix);

        // Acción cuando el usuario seleccione "hecho" en el botón del teclado virtual.
        binding.etxtMainLastName.setOnEditorActionListener((v, actionId, event) -> {
            modificarNumeroDePulsaciones();
            return true;
        });
    }

    @Override
    protected void onResume() {
        /*
            Acción cuando el usuario modifique el texto de los EditText.
            Se comprobará si existen o no caracteres y se arrojará un mensaje de
            error en caso de no existir ninguno.
         */
        super.onResume();

        /*
         * Añade los respectivos métodos de la clase TextWatcher a sus respectivos EditTexts.
         */
        binding.etxtMainName.addTextChangedListener(nameTextWatcher);
        binding.etxtMainLastName.addTextChangedListener(lastNameTextWatcher);

        // Acción cuando el usuario cambie el foco a EditText que controla el nombre.
        binding.etxtMainName.setOnFocusChangeListener((v, hasFocus) ->
                changeTextViewColor(hasFocus, binding.txtMainName, binding.txtMainNameChars));

        // Acción cuando el usuario cambie el foco a EditText que controla el apellido.
        binding.etxtMainLastName.setOnFocusChangeListener((v, hasFocus) ->
                changeTextViewColor(hasFocus, binding.txtMainLastName, binding.txtMainLastNameChars));
    }

    @Override
    protected void onPause() {
        super.onPause();

        /*
         * Reinicia el valor de las acciones de los campos que interactúan con el usuario.
         */
        binding.etxtMainName.removeTextChangedListener(nameTextWatcher);
        binding.etxtMainLastName.removeTextChangedListener(lastNameTextWatcher);

        binding.etxtMainName.setOnFocusChangeListener(null);
        binding.etxtMainLastName.setOnFocusChangeListener(null);

    }

    /**
     * Este método determina si es posible para la aplicación seguir saludando. Dependiendo
     * de diversos factores como si el usuario es premium, si los campos necesarios
     * están rellenos, etc.
     */
    private void modificarNumeroDePulsaciones() {

        if (checkValueOfForm(binding.etxtMainName)
                && checkValueOfForm(binding.etxtMainLastName)) {
            // Comprueba que los EditTexts estén rellenos. Si no lo están,
            // no se hace nada.

            if (isPremium) {
                // Para poder saludar el usuario debe ser premium o no haber alcanzado
                // el número máximo de saludos.
                saludar();

            } else if (counter < maxIteration) {
                counter++;
                actualizarNumeroSaludos();
                saludar();
            } else {
                // Si no se puede saludar, se debe comprobar una subcripción.
                showBuyPremium();
            }
        }
    }

    /**
     * El método comprueba si la cadena existente en el EditText está vacía.
     *
     * En caso de no estarla, elimina el error. En caso de estarla, muestra un mensaje
     * de error.
     * @param editText
     * @return un valor boleano.
     */
    private boolean checkValueOfForm(EditText editText) {
        if(!editText.getText().toString().equalsIgnoreCase("")) {
            editText.setError(null);
            return true;
        } else {
            editText.setError(getString(R.string.etxt_main_required));
            return false;
        }
    }

    /**
     * Si la aplicación pude saludar, este método se encarga de escribir el saludo.
     * Dicho saludo cambiará según si el usuario ha marcado la opción de saludo
     * formal o no.
     */
    private void saludar() {
        // Se obtiene el valor de los EditTexts
        String name = binding.etxtMainName.getText().toString();
        String lastName = binding.etxtMainLastName.getText().toString();

        hideSoftKeyboard(binding.btnMainGreet);

        if (isPolitely) { // Se comprueba si el saludo es informal o no.
            Toast.makeText(this,
                    getString(R.string.txt_main_saludoFormal, greetPrefix, name, lastName),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,
                    getString(R.string.txt_main_saludoInformal, name, lastName),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Cuando el usuario marca o desmarca el checkBox de saludo, se llama a esta
     * función. La cual cambia el valor del campo isPolitely al mismo
     * valor que el checkBox
     *
     * @param buttonView
     * @param isChecked
     */
    private void actualizarSaludo(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            isPolitely = true;
        } else {
            isPolitely = false;
        }
    }

    /**
     * Actualiza el número del saludo actual cuando el usuario saluda sin
     * ser premium
     */
    private void actualizarNumeroSaludos() {
        binding.txtMainGreetCounter.setText(getString(R.string.txt_main_d_of_10, counter, maxIteration));
        actualiarBarraProgresiva();
    }

    /**
     * Actualiza el progreso de la barra Progresiva, según el valor de
     * counter.
     */
    private void actualiarBarraProgresiva() {
        binding.prBMainHowGreet.setProgress(counter);
    }

    /**
     * Muestra un mensaje para comprar una subcripción premium cuando se alcanza
     * el número máximo de pruebas gratuitas.
     */
    private void showBuyPremium() {
        hideSoftKeyboard(binding.btnMainGreet);

        Toast.makeText(this,
                getString(R.string.txt_main_buy_premium),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Se activa al hacer uso del switch premium. Modifica el valor del campo
     * isPremium según el valor del switch.
     *
     * @param buttonView
     * @param isChecked
     */
    private void cambiarPremium(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            isPremium = true;
        } else {
            isPremium = false;
        }

        mostrarEsconderBarraProgresiva(isChecked);
        // Se llama al método para mostrar o esconder la barra, según convenga.
    }

    /**
     * Cuando es llamado, desde el método cambiar premium, esconderá o mostrará la barra
     * según el valor del booleano pasado por dicho método.
     *
     * @param isChecked
     */
    private void mostrarEsconderBarraProgresiva(boolean isChecked) {
        if (isChecked) {
            binding.llMainPackPrB.setVisibility(View.GONE);
        } else {
            binding.llMainPackPrB.setVisibility(View.VISIBLE);
        }
        counter = 0; // Reinicializa el contador.
        actualizarNumeroSaludos(); // Actualiza el número de saludo, reinicia a 0.
    }

    /**
     * Determina que RadioButton está pulsado y con ello, que prefijo se debe usar
     * cuando el saludo sea formal.
     *
     * @param radioGroup
     * @param i
     */
    private void selectGreetPrefix(RadioGroup radioGroup, int i) {
        if (i == binding.rbMainMr.getId()) {
            greetPrefix = getString(R.string.rb_main_mr);
            binding.imageMainAvatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_mr));
        } else if (i == binding.rbMainMrs.getId()) {
            greetPrefix = getString(R.string.rb_main_mrs);
            binding.imageMainAvatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_mrs));
        } else {
            greetPrefix = getString(R.string.rb_main_ms);
            binding.imageMainAvatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_ms));

        }
    }

    /**
     * El método comprueba si el invocador tiene el foco. Luego llama a la función
     * changeAllTextViewColor pasándole los TextViews necesarios y un color.
     * @param hasFocus
     * @param textViews
     */
    private void changeTextViewColor(boolean hasFocus, TextView... textViews) {
        if (hasFocus) {
            changeAllTextViewColor(R.color.colorAccent, textViews);
        } else {
            changeAllTextViewColor(R.color.textPrimary, textViews);
        }
    }

    /**
     * Esta función recibe un color y un varags de elementos TextViews.
     *
     * Modifica el color de todos los elementos pasados al color dado.
     * @param color
     * @param textViews
     */
    private void changeAllTextViewColor(int color, TextView... textViews) {
        for(TextView textView : textViews) {
            textView.setTextColor(getResources().getColor(color));
        }
    }

    /**
     * Configuración inicial de los estados de la aplicación, para determinar, entre
     * otras cosas, los colores de los elementos que reciben el foco al inicio del programa.
     */
    private void setupInitialState() {
        changeAllTextViewColor(R.color.colorAccent, binding.txtMainName, binding.txtMainNameChars);
    }



}// Fin de la clase