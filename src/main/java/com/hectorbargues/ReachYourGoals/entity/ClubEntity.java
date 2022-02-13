
package com.hectorbargues.ReachYourGoals.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "clubs")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ClubEntity implements Serializable {

        @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String localizacion;
    private Long imagen;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime fecha;
    
}
