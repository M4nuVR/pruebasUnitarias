package edu.ucc.juegosdemesa.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReglasDeNegocioTest {

    @Nested
    @DisplayName("Reglas de vigencia de reservas")
    class ReservasTests {

        @Test
        @DisplayName("Debe considerar una reserva activa como no vencida antes de la fecha límite")
        void reservaActivaNoSeConsideraVencidaAntesDeLaFechaLimite() {
            LocalDateTime creada = LocalDateTime.of(2026, 10, 1, 9, 0);
            Reserva reserva = new Reserva(
                    new Ejemplar("R-001", EstadoEjemplar.RESERVADO,
                            new Juego("Catan", "Estrategia", 3, 4, 60)),
                    new Usuario("2026001", "Ana", "ana@ucc.edu", Rol.ESTUDIANTE),
                    creada,
                    creada.plusHours(24)
            );

            assertFalse(reserva.estaVencida(creada.plusHours(23)));
        }

        @Test
        @DisplayName("Debe considerar una reserva activa como vencida en la fecha límite")
        void reservaActivaSeConsideraVencidaEnLaFechaLimite() {
            LocalDateTime creada = LocalDateTime.of(2026, 10, 1, 9, 0);
            Reserva reserva = new Reserva(
                    new Ejemplar("R-002", EstadoEjemplar.RESERVADO,
                            new Juego("Puerto Rico", "Estrategia", 2, 5, 90)),
                    new Usuario("2026002", "Luis", "luis@ucc.edu", Rol.PROFESOR),
                    creada,
                    creada.plusHours(24)
            );

            assertTrue(reserva.estaVencida(creada.plusHours(24)));
        }

        @Test
        @DisplayName("Debe ignorar la expiración cuando la reserva fue convertida")
        void reservaConvertidaNoSeConsideraVencida() {
            LocalDateTime creada = LocalDateTime.of(2026, 10, 1, 9, 0);
            Reserva reserva = new Reserva(
                    new Ejemplar("R-003", EstadoEjemplar.RESERVADO,
                            new Juego("Ticket to Ride", "Familia", 2, 5, 45)),
                    new Usuario("2026003", "Marta", "marta@ucc.edu", Rol.ESTUDIANTE),
                    creada,
                    creada.plusHours(1)
            );

            reserva.cambiarEstado(EstadoReserva.CONVERTIDA);

            assertFalse(reserva.estaVencida(creada.plusHours(2)));
            assertEquals(EstadoReserva.CONVERTIDA, reserva.getEstado());
        }
    }

    @Nested
    @DisplayName("Reglas de préstamos")
    class PrestamosTests {

        @Test
        @DisplayName("Debe mantener un préstamo nuevo activo y permitir transferir la posesión")
        void prestamoNuevoEsActivoYPuedeTransferirseLaPosesion() {
            Usuario profesor = new Usuario("P-01", "Profesor", "profesor@ucc.edu", Rol.PROFESOR);
            Usuario estudiante = new Usuario("E-01", "Estudiante", "estudiante@ucc.edu", Rol.ESTUDIANTE);
            Ejemplar ejemplar = new Ejemplar("P-100", EstadoEjemplar.DISPONIBLE,
                    new Juego("Azul", "Familia", 2, 4, 30));

            Prestamo prestamo = Prestamo.iniciar(ejemplar, profesor, "Entrega inicial");

            assertTrue(prestamo.estaActivo());
            assertNull(prestamo.getEstudiantePoseedorActual());

            prestamo.transferirA(estudiante);

            assertEquals(estudiante, prestamo.getEstudiantePoseedorActual());
            assertEquals("Entrega inicial", prestamo.getObservaciones());
        }
    }

    @Nested
    @DisplayName("Reglas de ejemplares")
    class EjemplarTests {

        @Test
        @DisplayName("Debe actualizar el estado del ejemplar y agregar el componente correctamente")
        void ejemplarActualizaEstadoYAgregaComponenteCorrectamente() {
            Juego juego = new Juego("Dominó", "Familiar", 2, 4, 20);
            Ejemplar ejemplar = new Ejemplar("E-200", EstadoEjemplar.DISPONIBLE, juego);
            Componente componente = new Componente("Fichas", 28);

            ejemplar.actualizarEstado(EstadoEjemplar.RESERVADO);
            ejemplar.agregarComponente(componente);

            assertEquals(EstadoEjemplar.RESERVADO, ejemplar.getEstado());
            assertEquals(1, ejemplar.getComponentes().size());
            assertEquals("Fichas", ejemplar.getComponentes().get(0).getNombre());
            assertEquals(28, ejemplar.getComponentes().get(0).getCantidad());
        }
    }
}
