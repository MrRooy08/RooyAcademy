package com.test.permissionusesjwt.entity;

import com.test.permissionusesjwt.enums.ActiveStatus;
import com.test.permissionusesjwt.enums.MediaType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "tai_nguyen_bai_giang")
public class Media {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    @Column (name = "ma_tai_nguyen")
    String id;

    @Column (name = "ten_tai_nguyen")
    String name;

    @Enumerated(EnumType.STRING)
    @Column ( name = "loai_tai_nguyen")
    MediaType mediaType;

    @Enumerated(EnumType.STRING)
    @Column ( name = "trang_thai_tai_nguyen")
    ActiveStatus activeStatus;

    @Column (name = "tieu_de")
    String title;

    @Column(name = "kich_thuoc_byte")
    Long size; // để hiển thị (KB, MB...)

    @Column (name = "url")
    String url;

    @CreationTimestamp  //tu tao tgian
    @Column(updatable = false, name = "ngay_tao")  //khong bi thay doi khi co bat ky su kien gi xay ra
    Timestamp createdAt;

    @UpdateTimestamp //tu cap nhat tgian
    @Column( name = "ngay_cap_nhat")
    Timestamp updatedAt;

    @ManyToOne
    @JoinColumn ( name = "ma_bai_giang")
    Lesson lesson;

}
