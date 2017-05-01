package beerbear.agendapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText eNombre, eID, eCorreo, eTelefono;
    String nombre, correo,telefono,ID;
    ImageView iImagen;
    int cont = 0;
    //private String FIREBASE_URL = "https://agendapp-8fc13.firebaseio.com/";

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference storageRef;
    private FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eNombre = (EditText) findViewById(R.id.eNombre);
        eID = (EditText) findViewById(R.id.eID);
        eCorreo = (EditText) findViewById(R.id.eMail);
        eTelefono = (EditText) findViewById(R.id.eTel);
        iImagen = (ImageView) findViewById(R.id.imagen);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

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
                //
                storageRef = storage.getReferenceFromUrl("gs://agendapp-8fc13.appspot.com").child("conectionProblems.png");
                final long ONE_MEGABYTE = 1024 * 1024;
                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Data for "images/island.jpg" is returns, use this as needed
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 ,bytes.length);
                        iImagen.setImageBitmap(bitmap);


                        Toast.makeText(getApplicationContext(), "si bajó", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Toast.makeText(getApplicationContext(), "Esa mierda no sirve", Toast.LENGTH_SHORT).show();

                    }
                });

                /*
                final File localFile;
                try {
                    localFile = File.createTempFile("imagen","png");
                    storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            iImagen.setImageBitmap(bitmap);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
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
                myRef = database.getReference().child(String.valueOf(id));
                myRef.removeValue();
                limpiar();
                break;
            case R.id.bBuscar:
                myRef = database.getReference("contactos").child(String.valueOf(ID));
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(ID).exists()){

                            Contactos contacto = new Contactos();
                            contacto = dataSnapshot.child(ID).getValue(Contactos.class);
                            Log.d("data",dataSnapshot.child(ID).getValue().toString());
                            eNombre.setText(contacto.getNombre());
                            eCorreo.setText(contacto.getCorreo());
                            eTelefono.setText(contacto.getTelefono());





                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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
