package ec.edu.ups.rest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.ejb.EJB;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ec.edu.ups.ejb.ClienteFacade;
import ec.edu.ups.ejb.ReservaFacade;
import ec.edu.ups.ejb.RestauranteFacade;
import ec.edu.ups.entidades.Cliente;
import ec.edu.ups.entidades.Reserva;
import ec.edu.ups.entidades.Restaurante;

@Path("/crear/")
public class crear {
	
	@EJB
	private ClienteFacade ejbCliente; 
	@EJB
	private ReservaFacade ejbReserva;
	@EJB
	private RestauranteFacade ejbResturante;

	public crear() {
		// TODO Auto-generated constructor stub
	}
	
	
	@POST
    @Path("/registro")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
	public Response registro(@FormParam("cedula") String cedula, @FormParam("nombre") String nombre, @FormParam("apellido") String apellido
	    		, @FormParam("telefono") String telefono, @FormParam("direccion") String direccion, @FormParam("correo")String correo){
			
		System.out.println(cedula);
        Jsonb jsonb = JsonbBuilder.create();
		if(nombre!=null && nombre.equals("")!=true) {
    		Cliente persona2= new Cliente(cedula, nombre, apellido, telefono, direccion, correo);
        	try {
        		String ok="Cliente creado";
        		
        		ejbCliente.create(persona2);
        		return Response.ok(jsonb.toJson(ok)).
                		header("Access-Control-Allow-Origin", "*")
    					.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
    					.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE").build();
        	}catch (Exception e) {
                return Response.status(500).entity("Error al registrar cliente " + e).build();

			}
        	        	
    	}
    	return Response.ok("Cliente creado correctamente").build();
    }
	
	@POST
    @Path("/crearrestaurante")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
	public Response crearrestaurante(@FormParam("nombre") String nombre, @FormParam("direccion") String direccion
	    		, @FormParam("telefono") String telefono, @FormParam("aforo") Integer aforo){
			
		System.out.println(nombre);
        Jsonb jsonb = JsonbBuilder.create();
        
		if(nombre!=null && nombre.equals("")!=true) {
			
			Restaurante rest= new Restaurante(nombre, direccion, telefono, aforo);        	
    		try {
        		String ok="Restaurante creado";
        		
        		ejbResturante.create(rest);
        		return Response.ok(jsonb.toJson(ok)).
                		header("Access-Control-Allow-Origin", "*")
    					.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
    					.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE").build();
        	}catch (Exception e) {
                return Response.status(500).entity("Error al registrar cliente " + e).build();

			}
        	        	
    	}
    	return Response.ok("restaurante response").build();
    }
	
	@POST
    @Path("/creareserva")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
	public Response creareserva(@FormParam("nombre") String nombreRestaurante,@FormParam("cedula") String cedula,
			@FormParam("numeroPersonas") Integer numeroPersonas, @FormParam("fechaIngreso")String fechaIngreso){
		
        Jsonb jsonb = JsonbBuilder.create();
			Restaurante restaurante = ejbResturante.buscarPorNombre(cedula);
			Cliente cliente = ejbCliente.buscarPorCedula(nombreRestaurante);
			System.out.println(cliente);
			System.out.println(restaurante);
		
		if (cliente != null && restaurante != null) {
			
			System.out.println(cliente);
			System.out.println(restaurante);
			
			int capacidad = restaurante.getAforo() - numeroPersonas;
			
			if (capacidad > 1) {
				
				System.out.println("Hasta aqui"+fechaIngreso);
				
				
				
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat formato =  new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
				restaurante.setAforo(capacidad);
				ejbResturante.edit(restaurante);
				
				
				Reserva reserva = new Reserva(cal, numeroPersonas, cliente,restaurante );
				
				ejbReserva.create(reserva);
				return Response.ok(jsonb.toJson(reserva)).
                		header("Access-Control-Allow-Origin", "*")
    					.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
    					.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE").build();
				
			}else {
                return Response.status(500).entity("No hay Mesa disponible " ).build();

			}
			
		}else {
			
	    	return Response.ok("No se encontro cliente tampoco restaurante").build();
	    	


	}
	}
}