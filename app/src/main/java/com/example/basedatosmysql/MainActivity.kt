package com.example.basedatosmysql

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import java.sql.SQLException

class MainActivity : AppCompatActivity() {

    //Objetos de componentes graficos
    private lateinit var etNumEmp: EditText
    private lateinit var etNombre: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etSueldo: EditText
    private lateinit var btnAgregar: ImageButton
    private lateinit var btnBuscar: ImageButton
    private lateinit var btnActualizar: ImageButton
    private lateinit var btnEliminar: ImageButton
    private lateinit var btnLista: Button

    //Objeto para gestion de la BD
    private lateinit var admin: ControladorBD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etNumEmp = findViewById(R.id.txtNumEmp)
        etNombre = findViewById(R.id.txtNombre)
        etApellidos = findViewById(R.id.txtApellidos)
        etSueldo = findViewById(R.id.txtSueldo)
        btnAgregar = findViewById(R.id.btnAgregar)
        btnBuscar = findViewById(R.id.btnBuscar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnActualizar = findViewById(R.id.btnActualizar)
        btnLista = findViewById(R.id.btnLista)

        //Creacin e objeto de clase Controlador Bd para crear la base de datos
        //Sus parametros son: contexto, nombre de la Base de Datos, null y version de la BD
        //La ruta de creacion de la base de datos en el dispositivo es:
        //android/<paquete>/databases/<nombre_bd>
        admin = ControladorBD(this, "empresapatito.db", null,1)


        //Eventos onClick
        btnAgregar.setOnClickListener { registrarEmpleado() }
        btnBuscar.setOnClickListener { buscarEmpleado() }
        btnActualizar.setOnClickListener { actualizarEmpleado() }
        btnEliminar.setOnClickListener { eliminarEmpleado() }
        btnLista.setOnClickListener{listarRegistro()}
    }

    private fun listarRegistro() {
        //Objeto para conectar a otra activity
        val intent = Intent(this, ListadoActivity::class.java)
        startActivity(intent)
    }

    private fun eliminarEmpleado() {
        //Establecer ek modo de apertura de la base de datos en modo escritura
        val bd = admin.writableDatabase

        //Variables para busqueda de dato y eliminar
        val nump = etNumEmp.text.toString()

        //Validar que exista el campo no este vacio
        if(nump.isNotEmpty()){
            //Variable que almacene el numero de registros borrados
            //La instruccion delete requiere parametros para realizar el borrado, estos son:
            //tabla, informacion por actualizar, clausala where condicion y sin parametros para la clausula
            val cantidad = bd.delete("empleado", "numep = $nump", null)
            bd.close()


            //Limpiar los campos de texto
            etNumEmp.setText("")
            etNombre.setText("")
            etApellidos.setText("")
            etSueldo.setText("")
            etNumEmp.requestFocus()

            //Validad si existieron registros a borrar
            if(cantidad >  0) Toast.makeText(this, "Empleado Eliminado",Toast.LENGTH_SHORT)
                .show() else Toast.makeText(
                    this,
                        "El numero de empleado no existe",
                    Toast.LENGTH_SHORT
                ).show()
        }else{
            Toast.makeText(this, " Ingresa numero de empleado",Toast.LENGTH_SHORT)
        }
    }

    private fun actualizarEmpleado() {
        //Establecer ek modo de apertura de la base de datos en modo escritura
        val bd = admin.writableDatabase

        //Variables para obtener los valores de componentes graficos
        val nump = etNumEmp.text.toString()
        val nomp = etNombre.text.toString()
        val apep = etApellidos.text.toString()
        val suep = etSueldo.text.toString()
        //Validar que exista informacion registrada
        if(nump.isNotEmpty() && nomp.isNotEmpty() && apep.isNotEmpty() && suep.isNotEmpty()) {
            //Objeto que almacene los valores para enviar a la tabla
            val registro = ContentValues()

            //Referencias a los datos que pasar a la BD
            //Indicando como parametros el put el nombre del campo y el valor a insertar
            //El segundo proviene de los campos de texto
            registro.put("numep", nump)
            registro.put("nombre", nomp)
            registro.put("apellidos", apep)
            registro.put("sueldo", suep)

            //Variable que indica el numero de registros actualizados
            //La instruccion update requiere parametros para realizar la actualizacion de datos
            //tabla, informacion por actualizar, clausala where condicion y sin parametros para la clausula
            val cantidad = bd.update("empleado", registro, "numep=$nump", null)

            //Cerrar la Bd
            bd.close()

            //Limpiar los campos de texto
            etNumEmp.setText("")
            etNombre.setText("")
            etApellidos.setText("")
            etSueldo.setText("")
            etNumEmp.requestFocus()

            //Validar si existieron registros a borrar
            if (cantidad > 0) Toast.makeText(
                this,
                "Datos del empleado actualizado",
                Toast.LENGTH_SHORT
            ).show() else Toast.makeText(
                this,
                "El numero de empleado no existe",
                Toast.LENGTH_SHORT
            ).show()
            Toast.makeText(this, "Empleado registrado", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Debes registrar primero los datos ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buscarEmpleado() {
        //Establacer el modo de apertura de la base datos en modo Escritura
        val bd = admin.readableDatabase

        //Variable para busqueda de dato para obtener informacion
        val nump = etNumEmp.text.toString()

            //Validar que el campo no este vacio
        if(nump.isNotEmpty()){
            //Objeto apunta al registro donde localice el dato, se le envia la instruccion sql de busqueda
            val fila = bd.rawQuery(
                "select nombre, apellidos, sueldo from empleado " +
                        "where numep =" + nump, null
            )
            //Validar
            if(fila.moveToFirst()){
                //Se coloca en los componentes los valores encontrados
                etNombre.setText(fila.getString(0))
                etApellidos.setText(fila.getString(1))
                etSueldo.setText(fila.getString(2))
                //Se cierra la base de datos
                bd.close()
            }else{
                Toast.makeText(this,"Numero de empleado no existe",Toast.LENGTH_SHORT).show()
                etNumEmp.setText("")
                etNumEmp.requestFocus()
                bd.close()
            }
        }else{
            Toast.makeText(this,"Ingresa numero de empleado",Toast.LENGTH_SHORT).show()
            etNumEmp.requestFocus()

        }
    }

    private fun registrarEmpleado() {
        //Establecer ek modo de apertura de la base de datos en modo escritura
        val bd = admin.writableDatabase

        //Variables para obtener los valores de componentes graficos
        val nump = etNumEmp.text.toString()
        val nomp = etNombre.text.toString()
        val apep = etApellidos.text.toString()
        val suep = etSueldo.text.toString()
        //Validar que exista informacion registrada
        if(nump.isNotEmpty() && nomp.isNotEmpty() && apep.isNotEmpty() && suep.isNotEmpty()){
            //Objeto que almacene los valores para enviar a la tabla
            val registro = ContentValues()

            //Referencias a los datos que pasar a la BD
            //Indicando como parametros el put el nombre del campo y el valor a insertar
            //El segundo proviene de los campos de texto
            registro.put("numep", nump)
            registro.put("nombre", nomp)
            registro.put("apellidos", apep)
            registro.put("sueldo", suep)
            if(bd != null){
                //Almacenar los valores en la tabla
                try{
                    val x: Long = bd.insert("empleado",null,registro)
                }catch (e: SQLException){
                    Log.e("Exception","Error"+e.message.toString())
                }
                //Cerrar la Bd
                bd.close()
            }
            //Limpiar los campos de texto
            etNumEmp.setText("")
            etNombre.setText("")
            etApellidos.setText("")
            etSueldo.setText("")
            etNumEmp.requestFocus()

            Toast.makeText(this, "Empleado registrado", Toast.LENGTH_SHORT).show()

        }else{
            Toast.makeText(this, "Debes registrar primero los datos ", Toast.LENGTH_SHORT).show()
        }
    }
}