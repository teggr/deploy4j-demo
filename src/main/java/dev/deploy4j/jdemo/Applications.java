package dev.deploy4j.jdemo;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class Applications {

  private final JdbcTemplate jdbcTemplate;

  public record ApplicationRecord(Long id, String name, String repositoryPath) {}

  public java.util.List<ApplicationRecord> listApplications() {
    return jdbcTemplate.query(
      "SELECT id, name, repository_path FROM application",
      (rs, rowNum) -> new ApplicationRecord(rs.getLong("id"), rs.getString("name"), rs.getString("repository_path"))
    );
  }

  public ApplicationRecord getApplication(Long id) {
    return jdbcTemplate.queryForObject(
      "SELECT id, name, repository_path FROM application WHERE id = ?",
      (rs, rowNum) -> new ApplicationRecord(rs.getLong("id"), rs.getString("name"), rs.getString("repository_path")),
      id
    );
  }

  @Transactional
  public void addApplication(String name) {
    jdbcTemplate.update("INSERT INTO application (name) VALUES (?)", name);
  }

  @Transactional
  public void updateApplication(Long id, String name) {
    jdbcTemplate.update("UPDATE application SET name = ? WHERE id = ?", name, id);
  }

  @Transactional
  public void updateRepositoryPath(Long id, String repositoryPath) {
    jdbcTemplate.update("UPDATE application SET repository_path = ? WHERE id = ?", repositoryPath, id);
  }

  @Transactional
  public void deleteApplication(Long id) {
    jdbcTemplate.update("DELETE FROM application WHERE id = ?", id);
  }

}
