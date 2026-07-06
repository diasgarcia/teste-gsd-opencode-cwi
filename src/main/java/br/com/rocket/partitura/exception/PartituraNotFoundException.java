package br.com.rocket.partitura.exception;

public class PartituraNotFoundException extends RuntimeException {

    public PartituraNotFoundException(Long id) {
        super("Partitura não encontrada com o ID: " + id);
    }

    public PartituraNotFoundException(String message) {
        super(message);
    }

}
