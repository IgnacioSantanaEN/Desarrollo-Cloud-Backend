package api.Azure.config;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JacksonJsonSerde;

import api.Azure.model.AlertaFraude;
import api.Azure.model.Transaccion;

@Configuration
public class PipelineTopology {
    @Bean
    public KStream<String,Transaccion> kStreamPipeline(StreamsBuilder builder) {
        JacksonJsonSerde<Transaccion> txSerde = new JacksonJsonSerde<>(Transaccion.class);
        txSerde.deserializer().addTrustedPackages("*");

        JacksonJsonSerde<AlertaFraude> alertSerde = new JacksonJsonSerde<>(AlertaFraude.class);
        alertSerde.deserializer().addTrustedPackages("*");

        KStream<String,Transaccion> entrada = builder.stream(
            "banco-transacciones", 
            Consumed.with(Serdes.String(), 
            txSerde));

        KStream<String,Transaccion> validas = entrada
            .filter((key,tx) -> tx != null && tx.getMonto() != null
            && tx.getMonto() > 10)
            .peek((key,tx) -> System.out.println("Procesada: " + tx.getMonto()));

        KStream<String,AlertaFraude> alertas = validas
            .filter((key,tx) -> tx.getMonto() > 10000)
            .mapValues(tx -> new AlertaFraude(tx.getUsuario(),tx.getMonto(),
            "Transaccion inusualmente alta en " + tx.getOrigen(),"ALTA"));

        alertas.peek((key,a) -> System.out.println("ALERTA: " + a.getMotivo()))
            .to("banco-alertas",Produced.with(Serdes.String(),alertSerde));
            
        return entrada;
    }
}
