package com.healthtechbd.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(unique = true)
  public String token;

  @CreationTimestamp
  @Column(updatable = false)
  private Timestamp timeStamp;

  @Column(updatable = false)
  private LocalDateTime expireTime;

  @ManyToOne
  @JoinColumn(name = "appUser_id", referencedColumnName = "id")
  public AppUser appUser;

  public boolean isExpired() {
    return getExpireTime().isBefore(LocalDateTime.now());
  }
}
