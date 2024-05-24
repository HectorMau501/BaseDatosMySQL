package com.example.basedatosmysql

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class ListadoActivity : AppCompatActivity() {

    //Instancias de componentes
    private lateinit var etListado: EditText
    private lateinit var btnRegresar: Button
    private lateinit var admin: ControladorBD

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado)

        //Asociar la instancia con el componente
        etListado = findViewById(R.id.editDetalle)
        btnRegresar = findViewById(R.id.btnRegresar)
        //Creacion de la base de datos, de manera local, cuyo parametros son:
        //contexto de la aplicacion, nomre de la bd, version
        admin = ControladorBD(this, "empresapatito.db", null, 1)
        //define el modo de acceso a la bd
        val bd = admin.readableDatabase
        //Insrancia del apuntador al registro de busqueda
        val registro = bd.rawQuery("select * from empleado order by numep", null)
        //Variable para la cantidad de registro obtenidos
        val n = registro.count
        //Variable para control de datos en el TextView
        var nr = 0
        //Valido que existan registros de la bd
        if(n > 0){
            //Mover el cursor al inicio de los registro obtenidos
            registro.moveToFirst()
            etListado.text.insert(0,"")
            //Ciclo repetitivo para colocar la informavion dentro del TextView
            do{
                etListado.text.insert(nr, "Numero:  ${registro.getString(0)}"+
                    "\nNombre: ${registro.getString(1)}"+
                    "\nApellidos: ${registro.getString(2)}"+
                    "\nSueldo: ${registro.getString(3)}\n")
                nr++
            }while(registro.moveToNext())//Si existen mas registros
        }else{
            //Mensaje informativo que no hay campos
            Toast.makeText(this, "Sin registro de empleados",Toast.LENGTH_SHORT).show()
        }

        bd.close()

        //Evento onClick
        btnRegresar.setOnClickListener{
            //Objjeto para conectar a otra Activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}