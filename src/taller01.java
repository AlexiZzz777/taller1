// Diego Cortes - 22.376.295-6 - ICCI


import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;

public class taller01 {

    //Capacidad maxima de los vectores
    static int MAX = 100;
    
    //Vectores Paralelos de Alumnos
    static String[] alNombres = new String[MAX];
    static String[] alApellidos = new String[MAX];
    static String[] alRuts = new String[MAX];
    static String[] alParalelos = new String[MAX];

    //Vectores Paralelos de Solicitudes
    static String[] solNombres = new String[MAX];
    static String[] solApellidos = new String[MAX];

    //Vectores Paralelos de Admitidos al Grupo
    static String[] admNombres = new String[MAX];
    static String[] admApellidos = new String[MAX];
    static String[] admRuts = new String[MAX];
    static String[] admParalelos = new String[MAX];

    //Vector de Rechazados
    static String[] rechazadosTextos = new String[MAX];

    //Variables globales para estadisticas y control de versiones
    static int totalIntentos = 0;
    static int versC1 = 1;
    static int versC2 = 1;
    static int versRechazados = 1;
    static boolean archivosCargados = false;
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean salir = false;

        while (!salir) {
            System.out.println("\n===== Sistema de Control del Grupo POO =====");
            System.out.println("1) Cargar archivos (Alumnos y Solicitudes)");
            System.out.println("2) Procesar solicitudes (Filtrado automatico)");
            System.out.println("3) Inscripcion manual al grupo");
            System.out.println("4) Administracion del curso");
            System.out.println("5) Generar reportes");
            System.out.println("6) Analisis estadistico");
            System.out.println("7) Salir");
            System.out.print("Ingrese opcion: ");

            String input = scanner.nextLine().trim();
            int opcion = -1;

            if (!input.isEmpty()) {
                try {
                    opcion = Integer.parseInt(input);
                } catch (Exception e) {
                }
            }

            if (opcion >= 1 && opcion <= 7) {
                switch (opcion) {
                    case 1:
                        cargarArchivos();
                        break;
                    case 2:
                        if (archivosCargados) procesarSolicitudes();
                        else System.out.println("-> Error: Debe cargar los archivos primero (Opcion 1).");
                        break;
                    case 3:
                        if (archivosCargados) menuInscripcionManual();
                        else System.out.println("-> Error: Debe cargar los archivos primero (Opcion 1).");
                        break;
                    case 4:
                        if (archivosCargados) menuAdministracion();
                        else System.out.println("-> Error: Debe cargar los archivos primero (Opcion 1).");
                        break;
                    case 5:
                        if (archivosCargados) menuReportes();
                        else System.out.println("-> Error: Debe cargar los archivos primero (Opcion 1).");
                        break;
                    case 6:
                        if (archivosCargados) mostrarEstadisticas();
                        else System.out.println("-> Error: Debe cargar los archivos primero (Opcion 1).");
                        break;
                    case 7:
                        salir = true;
                        System.out.println("Saliendo del sistema...");
                        break;
                }
            } else {
                System.out.println("-> Error: Opcion invalida. Debe ingresar un numero entre 1 y 7.");
            }
        }
    }
    //cargar arch
    public static void cargarArchivos() {
        int cantAl = 0;
        int cantSol = 0;

        //Cargar Alumnos.txt
        try {
            File fAlumnos = new File("src/txt/Alumnos.txt");
            if (fAlumnos.exists()) {
                Scanner sAl = new Scanner(fAlumnos);
                while (sAl.hasNextLine()) {
                    String linea = sAl.nextLine().trim();
                    if (!linea.isEmpty()) {
                        String[] partes = linea.split(";");
                        if (partes.length == 4) {
                            int pos = buscarEspacioVacio(alNombres);
                            if (pos != -1) {
                                alNombres[pos] = partes[0].trim();
                                alApellidos[pos] = partes[1].trim();
                                alRuts[pos] = partes[2].trim();
                                alParalelos[pos] = partes[3].trim().toUpperCase();
                                cantAl++;
                            } else {
                                System.out.println("-> Error: Capacidad maxima de alumnos alcanzada.");
                            }
                        }
                    }
                }
                sAl.close();
            } else {
                System.out.println("-> Advertencia: No se encontro el archivo Alumnos.txt");
            }
        } catch (IOException e) {
            System.out.println("-> Error leyendo Alumnos.txt: " + e.getMessage());
        }
        //Cargar Solicitudes.txt
        try {
            File fSol = new File("src/txt/Solicitudes.txt");
            if (fSol.exists()) {
                Scanner sSol = new Scanner(fSol);
                while (sSol.hasNextLine()) {
                    String linea = sSol.nextLine().trim();
                    if (!linea.isEmpty()) {
                        String[] partes = linea.split("-");
                        if (partes.length == 2) {
                            int pos = buscarEspacioVacio(solNombres);
                            if (pos != -1) {
                                solNombres[pos] = partes[0].trim();
                                solApellidos[pos] = partes[1].trim();
                                cantSol++;
                            } else {
                                System.out.println("-> Error: Capacidad maxima de solicitudes alcanzada.");
                            }
                        }
                    }
                }
                sSol.close();
            } else {
                System.out.println("-> Advertencia: No se encontro el archivo Solicitudes.txt");
            }
        } catch (IOException e) {
            System.out.println("-> Error leyendo Solicitudes.txt: " + e.getMessage());
        }

        archivosCargados = true;
        System.out.println("Archivos cargados con exito!");
        System.out.println("- " + cantAl + " alumnos en la lista.");
        System.out.println("- " + cantSol + " solicitudes de ingreso.");
    }
    //solicitudes
    public static void procesarSolicitudes() {
        System.out.println("Procesando solicitudes...");
        int admitidosHoy = 0, rechazadosHoy = 0;

        for (int i = 0; i < MAX; i++) {
            if (solNombres[i] != null) {
                totalIntentos++;
                String nombreSol = solNombres[i];
                String apellidoSol = solApellidos[i];

                int idxAlumno = buscarAlumnoPorNombre(nombreSol, apellidoSol);

                if (idxAlumno != -1) { //Existe en la lista
                    String rut = alRuts[idxAlumno];
                    if (!estaAdmitido(rut)) {
                        agregarAdmitido(idxAlumno);
                        System.out.println("[OK]       " + nombreSol + " " + apellidoSol + " -> admitido en " + alParalelos[idxAlumno]);
                        admitidosHoy++;
                    } else {
                        System.out.println("[DUPLICADO] " + nombreSol + " " + apellidoSol + " ya estaba en el grupo.");
                    }
                } else { //No existe
                    agregarRechazado(nombreSol + " " + apellidoSol + " - No pertenece a ningun paralelo del curso");
                    System.out.println("[RECHAZO]  " + nombreSol + " " + apellidoSol + " -> no pertenece a ningun paralelo");
                    rechazadosHoy++;
                }
                //Borrar la solicitud
                solNombres[i] = null;
                solApellidos[i] = null;
            }
        }
        System.out.println("\nResumen: " + admitidosHoy + " admitidos / " + rechazadosHoy + " rechazados.");
    }

    //inscripcion manual 
    public static void menuInscripcionManual() {
        System.out.println("\nComo desea inscribir a la persona?");
        System.out.println("1) Por nombre completo");
        System.out.println("2) Por RUT");
        System.out.println("3) Volver");
        System.out.print("Ingrese opcion: ");

        String input = scanner.nextLine().trim();
        if (input.equals("1")) {
            System.out.print("Ingrese Nombre: ");
            String nom = scanner.nextLine().trim();
            System.out.print("Ingrese Apellido: ");
            String ape = scanner.nextLine().trim();

            if (!nom.isEmpty() && !ape.isEmpty()) {
                totalIntentos++;
                int idx = buscarAlumnoPorNombre(nom, ape);
                if (idx != -1) {
                    if (!estaAdmitido(alRuts[idx])) {
                        agregarAdmitido(idx);
                        System.out.println("Inscrito exitosamente en " + alParalelos[idx]);
                    } else {
                        System.out.println("El alumno ya estaba en el grupo.");
                    }
                } else {
                    agregarRechazado(nom + " " + ape + " - No pertenece a ningun paralelo del curso");
                    System.out.println("No pertenece a la lista oficial. Registrado en rechazados.");
                }
            } else {
                System.out.println("-> Error: Los campos no pueden estar vacios.");
            }
        } else if (input.equals("2")) {
            System.out.print("Ingrese RUT: ");
            String rut = scanner.nextLine().trim();

            if (!rut.isEmpty()) {
                totalIntentos++;
                int idx = buscarAlumnoPorRut(rut);
                if (idx != -1) {
                    if (!estaAdmitido(rut)) {
                        agregarAdmitido(idx);
                        System.out.println("Inscrito exitosamente en " + alParalelos[idx]);
                    } else {
                        System.out.println("El alumno ya estaba en el grupo.");
                    }
                } else {
                    agregarRechazado("Sin nombre registrado, RUT: " + rut);
                    System.out.println("El RUT " + rut + " no pertenece a ningun paralelo del curso.");
                    System.out.println("No tenemos su nombre, por lo que se registrara solo el RUT en los rechazados.");
                }
            } else {
                System.out.println("-> Error: El RUT no puede estar vacio.");
            }
        } else if (!input.equals("3")) {
            System.out.println("-> Error: Opcion invalida.");
        }
    }

    //admi del curso
    public static void menuAdministracion() {
        System.out.println("\n--- Administracion del curso ---");
        System.out.println("1) Cambiar paralelo de un alumno");
        System.out.println("2) Eliminar alumno del curso");
        System.out.println("3) Inscribir alumno nuevo");
        System.out.println("4) Volver");
        System.out.print("Ingrese opcion: ");

        String input = scanner.nextLine().trim();

        if (input.equals("1")) {
            System.out.print("Ingrese RUT del alumno: ");
            String rut = scanner.nextLine().trim();
            int idx = buscarAlumnoPorRut(rut);
            if (idx != -1) {
                System.out.println("Alumno: " + alNombres[idx] + " " + alApellidos[idx] + " (actualmente en " + alParalelos[idx] + ")");
                System.out.print("Nuevo paralelo (C1/C2): ");
                String nuevoPara = scanner.nextLine().trim().toUpperCase();
                
                if (nuevoPara.equals("C1") || nuevoPara.equals("C2")) {
                    alParalelos[idx] = nuevoPara;
                    //Actualizar si esta en el grupo
                    for (int i = 0; i < MAX; i++) {
                        if (admRuts[i] != null && admRuts[i].equalsIgnoreCase(rut)) {
                            admParalelos[i] = nuevoPara;
                            break;
                        }
                    }
                    guardarAlumnosTxt();
                    System.out.println("Paralelo actualizado! Cambios guardados en Alumnos.txt");
                } else {
                    System.out.println("-> Error: Paralelo invalido. Solo se permite C1 o C2.");
                }
            } else {
                System.out.println("-> Error: Alumno no encontrado.");
            }
        } else if (input.equals("2")) {
            System.out.print("Ingrese RUT del alumno a eliminar: ");
            String rut = scanner.nextLine().trim();
            int idx = buscarAlumnoPorRut(rut);
            if (idx != -1) {
                alNombres[idx] = null;
                alApellidos[idx] = null;
                alRuts[idx] = null;
                alParalelos[idx] = null;

                //Sacar del grupo si estaba
                for (int i = 0; i < MAX; i++) {
                    if (admRuts[i] != null && admRuts[i].equalsIgnoreCase(rut)) {
                        admNombres[i] = null;
                        admApellidos[i] = null;
                        admRuts[i] = null;
                        admParalelos[i] = null;
                        break;
                    }
                }
                guardarAlumnosTxt();
                System.out.println("Alumno eliminado! Cambios guardados en Alumnos.txt");
            } else {
                System.out.println("-> Error: Alumno no encontrado.");
            }
        } else if (input.equals("3")) {
            int pos = buscarEspacioVacio(alNombres);
            if (pos != -1) {
                System.out.print("Ingrese Nombre: ");
                String nom = scanner.nextLine().trim();
                System.out.print("Ingrese Apellido: ");
                String ape = scanner.nextLine().trim();
                System.out.print("Ingrese RUT: ");
                String rut = scanner.nextLine().trim();
                System.out.print("Ingrese Paralelo (C1/C2): ");
                String par = scanner.nextLine().trim().toUpperCase();

                if (!nom.isEmpty() && !ape.isEmpty() && !rut.isEmpty() && (par.equals("C1") || par.equals("C2"))) {
                    if (buscarAlumnoPorRut(rut) == -1) {
                        alNombres[pos] = nom;
                        alApellidos[pos] = ape;
                        alRuts[pos] = rut;
                        alParalelos[pos] = par;
                        guardarAlumnosTxt();
                        System.out.println("Alumno inscrito a la lista! Cambios guardados en Alumnos.txt");
                    } else {
                        System.out.println("-> Error: El RUT ya existe en el curso.");
                    }
                } else {
                    System.out.println("-> Error: Datos invalidos. Verifique no dejar espacios vacios y usar C1 o C2.");
                }
            } else {
                System.out.println("-> Error: Capacidad maxima del curso alcanzada (100).");
            }
        } else if (!input.equals("4")) {
            System.out.println("-> Error: Opcion invalida.");
        }
    }

    public static void guardarAlumnosTxt() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("src/txt/Alumnos.txt"));
            for (int i = 0; i < MAX; i++) {
                if (alNombres[i] != null) {
                    bw.write(alNombres[i] + ";" + alApellidos[i] + ";" + alRuts[i] + ";" + alParalelos[i]);
                    bw.newLine();
                }
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("-> Error al guardar Alumnos.txt: " + e.getMessage());
        }
    }

    //reportes
    public static void menuReportes() {
        File dir = new File("Reportes");
        if (!dir.exists()) {
            dir.mkdir();
        }

        System.out.println("\n--- Generar Reportes ---");
        System.out.println("1) Reporte Paralelo C1");
        System.out.println("2) Reporte Paralelo C2");
        System.out.println("3) Reporte Rechazados");
        System.out.println("4) Volver");
        System.out.print("Ingrese opcion: ");

        String input = scanner.nextLine().trim();

        try {
            if (input.equals("1")) {
                File file = new File("Reportes/ReporteC1-V" + versC1 + ".txt");
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write("=== Miembros del grupo - Paralelo C1 ===\n");
                for (int i = 0; i < MAX; i++) {
                    if (admNombres[i] != null && admParalelos[i].equalsIgnoreCase("C1")) {
                        bw.write(admNombres[i] + " " + admApellidos[i] + " - " + admRuts[i] + "\n");
                    }
                }
                bw.close();
                System.out.println("Reporte generado: Reportes/ReporteC1-V" + versC1 + ".txt");
                versC1++;
            } else if (input.equals("2")) {
                File file = new File("Reportes/ReporteC2-V" + versC2 + ".txt");
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write("=== Miembros del grupo - Paralelo C2 ===\n");
                for (int i = 0; i < MAX; i++) {
                    if (admNombres[i] != null && admParalelos[i].equalsIgnoreCase("C2")) {
                        bw.write(admNombres[i] + " " + admApellidos[i] + " - " + admRuts[i] + "\n");
                    }
                }
                bw.close();
                System.out.println("Reporte generado: Reportes/ReporteC2-V" + versC2 + ".txt");
                versC2++;
            } else if (input.equals("3")) {
                File file = new File("Reportes/Rechazados-V" + versRechazados + ".txt");
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write("=== Solicitudes rechazadas ===\n");
                for (int i = 0; i < MAX; i++) {
                    if (rechazadosTextos[i] != null) {
                        bw.write(rechazadosTextos[i] + "\n");
                    }
                }
                bw.close();
                System.out.println("Reporte generado: Reportes/Rechazados-V" + versRechazados + ".txt");
                versRechazados++;
            } else if (!input.equals("4")) {
                System.out.println("-> Error: Opcion invalida.");
            }
        } catch (IOException e) {
            System.out.println("-> Error al escribir el reporte: " + e.getMessage());
        }
    }
    //estadisticas
    public static void mostrarEstadisticas() {
        int cantRechazados = 0;
        for (int i = 0; i < MAX; i++) {
            if (rechazadosTextos[i] != null) cantRechazados++;
        }

        int admC1 = 0, admC2 = 0;
        for (int i = 0; i < MAX; i++) {
            if (admNombres[i] != null) {
                if (admParalelos[i].equalsIgnoreCase("C1")) admC1++;
                else if (admParalelos[i].equalsIgnoreCase("C2")) admC2++;
            }
        }

        int totalAdmitidos = admC1 + admC2;
        double porcRechazo = 0.0;
        double tasaAdmision = 0.0;

        if (totalIntentos > 0) {
            porcRechazo = ((double) cantRechazados / totalIntentos) * 100.0;
            tasaAdmision = ((double) totalAdmitidos / totalIntentos) * 100.0;
        }

        System.out.println("\n--- Analisis estadistico ---");
        System.out.println("Total de intentos de ingreso: " + totalIntentos);
        System.out.printf("Rechazados: %d (%.1f%%)\n", cantRechazados, porcRechazo);
        System.out.println("Admitidos por paralelo -> C1: " + admC1 + " | C2: " + admC2);
        System.out.printf("Tasa de admision: %.1f%%\n", tasaAdmision);
    }

    //metodos aux
    public static int buscarEspacioVacio(String[] arreglo) {
        for (int i = 0; i < MAX; i++) {
            if (arreglo[i] == null) {
                return i;
            }
        }
        return -1; //No hay espacio
    }

    public static int buscarAlumnoPorNombre(String nom, String ape) {
        for (int i = 0; i < MAX; i++) {
            if (alNombres[i] != null && alNombres[i].equalsIgnoreCase(nom) && alApellidos[i].equalsIgnoreCase(ape)) {
                return i;
            }
        }
        return -1;
    }

    public static int buscarAlumnoPorRut(String rut) {
        for (int i = 0; i < MAX; i++) {
            if (alRuts[i] != null && alRuts[i].equalsIgnoreCase(rut)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean estaAdmitido(String rut) {
        for (int i = 0; i < MAX; i++) {
            if (admRuts[i] != null && admRuts[i].equalsIgnoreCase(rut)) {
                return true;
            }
        }
        return false;
    }

    public static void agregarAdmitido(int idxAlumno) {
        int pos = buscarEspacioVacio(admNombres);
        if (pos != -1) {
            admNombres[pos] = alNombres[idxAlumno];
            admApellidos[pos] = alApellidos[idxAlumno];
            admRuts[pos] = alRuts[idxAlumno];
            admParalelos[pos] = alParalelos[idxAlumno];
        } else {
            System.out.println("-> Error: Lista de admitidos llena (Max 100).");
        }
    }

    public static void agregarRechazado(String texto) {
        int pos = buscarEspacioVacio(rechazadosTextos);
        if (pos != -1) {
            rechazadosTextos[pos] = texto;
        } else {
            System.out.println("-> Error: Lista de rechazados llena (Max 100).");
        }
    }
}