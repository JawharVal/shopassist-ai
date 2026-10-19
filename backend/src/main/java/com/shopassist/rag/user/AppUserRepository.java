package com.shopassist.rag.user;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AppUserRepository {

    private final JdbcClient jdbcClient;

    public AppUserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<AppUser> findByEmail(String email) {
        return jdbcClient.sql("select id, email, password_hash, tenant_id, role from app_user where email = :email")
                .param("email", email)
                .query((rs, i) -> new AppUser(
                        rs.getLong("id"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("tenant_id"),
                        rs.getString("role")
                ))
                .optional();
    }
    public List<AppUser> findAll() {
        return jdbcClient.sql("select id, email, password_hash, tenant_id, role from app_user")
                .query((rs, i) -> new AppUser(
                        rs.getLong("id"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getString("tenant_id"),
                        rs.getString("role")
                ))
                .list();
    }
    public int deleteByEmail(String email) {
        return jdbcClient.sql("delete from app_user where email = :email")
                .param("email", email)
                .update();
    }

    public long count() {
        Long count = jdbcClient.sql("select count(*) from app_user").query(Long.class).single();
        return count == null ? 0L : count;
    }
    public void updateRole(String email, String role) {
        jdbcClient.sql("update app_user set role = :role where email = :email")
                .param("role", role)
                .param("email", email)
                .update();
    }
    public void insert(String email, String passwordHash, String tenantId, String role) {
        jdbcClient.sql("insert into app_user(email, password_hash, tenant_id, role) values (:email, :passwordHash, :tenantId, :role)")
                .param("email", email)
                .param("passwordHash", passwordHash)
                .param("tenantId", tenantId)
                .param("role", role)
                .update();
    }
}
