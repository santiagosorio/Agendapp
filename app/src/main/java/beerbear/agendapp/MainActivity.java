package beerbear.agendapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText eNombre, eID, eCorreo, eTelefono;
    String nombre, correo,telefono,ID;
    int cont = 0;
    //private String FIREBASE_URL = "https://agendapp-8fc13.firebaseio.com/";

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eNombre = (EditText) findViewById(R.id.eNombre);
        eID = (EditText) findViewById(R.id.eID);
        eCorreo = (EditText) findViewById(R.id.eMail);
        eTelefono = (EditText) findViewById(R.id.eTel);

        database = FirebaseDatabase.getInstance();

    }


    public void onClick(View view) {
        int id = view.getId();

        nombre = eNombre.getText().toString();
        ID = eID.getText().toString();
        telefono = eTelefono.getText().toString();
        correo = eCorreo.getText().toString();

        switch (id){
            case R.id.bGuardar:
                myRef = database.getReference();
                Contactos contacto = new Contactos (ID,nombre,telefono,correo);
                myRef.child("contactos").child(String.valueOf(cont)).setValue(contacto);
                cont++;
                limpiar();
                break;
            case R.id.bActualizar:
                myRef = database.getReference("contactos").child(String.valueOf(ID));
                Map<String, Object> nuevonombre = new HashMap<>();
                nuevonombre.put("nombre",nombre);
                myRef.updateChildren(nuevonombre);
                limpiar();
                break;
            case R.id.bBorrar:
                myRef = database.getReference();
                myRef.removeValue();
                limpiar();
                break;
            case R.id.bBuscar:
                myRef = database.getReference("contactos").child(String.valueOf(ID));
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(ID).exists()){
                            Log.d("data",dataSnapshot.child(ID).getValue().toString());
                            Contactos contacto = new Contactos();
                            contacto = dataSnapshot.child(ID).getValue(Contactos.class);
                            eNombre.setText(contacto.getNombre());
                            eCorreo.setText(contacto.getCorreo());
                            eTelefono.setText(contacto.getTelefono());

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                limpiar();
                break;

        }

    }

    private void limpiar() {
        eTelefono.setText("");
        eCorreo.setText("");
        eID.setText("");
        eNombre.setText("");
    }


}
