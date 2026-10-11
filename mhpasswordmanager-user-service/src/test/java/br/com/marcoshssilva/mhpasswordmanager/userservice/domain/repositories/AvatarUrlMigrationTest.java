package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import static org.assertj.core.api.Assertions.assertThat;

class AvatarUrlMigrationTest {
    @Test
    void migrationPreservesExistingUrlsAndAllowsLongOnes() throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:h2:mem:avatar_migration;MODE=PostgreSQL");
             var statement = connection.createStatement();
             var sql = new ClassPathResource("db/migration/V10__EXPAND_AVATAR_URL.sql").getInputStream()) {
            statement.execute("CREATE TABLE users_details (imageurl varchar(255))");
            statement.execute("INSERT INTO users_details VALUES ('https://old.example/avatar.png')");
            statement.execute(new String(sql.readAllBytes(), StandardCharsets.UTF_8));
            try (var row = statement.executeQuery("SELECT imageurl FROM users_details")) {
                assertThat(row.next()).isTrue();
                assertThat(row.getString(1)).isEqualTo("https://old.example/avatar.png");
            }
            try (var update = connection.prepareStatement("UPDATE users_details SET imageurl = ?")) {
                update.setString(1, "x".repeat(2048));
                assertThat(update.executeUpdate()).isEqualTo(1);
            }
        }
    }
}
