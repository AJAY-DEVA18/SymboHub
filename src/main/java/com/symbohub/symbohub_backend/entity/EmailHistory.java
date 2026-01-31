package com.symbohub.symbohub_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brochure_title")
    private String brochureTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brochure_id")
    private Brochure brochure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "receiver_email", nullable = false)
    private String receiverEmail;

    @Column(name = "subject")
    private String subject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmailStatus status;

    @Column(name = "sent_date")
    private LocalDateTime sentDate;

    @Column(name = "delivered_date")
    private LocalDateTime deliveredDate;

    @Column(name = "read_date")
    private LocalDateTime readDate;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @PrePersist
    protected void onCreate() {
        this.sentDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = EmailStatus.SENT;
        }
    }

    public enum EmailStatus {
        PENDING, SENT, DELIVERED, FAILED, READ
    }
}