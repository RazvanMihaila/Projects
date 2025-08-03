<?php
class Imagine {
    private $conn;
    private $table = 'imagini';

    public function __construct($db) {
        $this->conn = $db;
    }

    public function getAll() {
        $sql = "SELECT * FROM " . $this->table;
        $result = $this->conn->query($sql);
        return $result;
    }

    public function getById($id) {
        $sql = "SELECT * FROM " . $this->table . " WHERE id=?";
        $stmt = $this->conn->prepare($sql);
        $stmt->bind_param("i", $id);
        $stmt->execute();
        return $stmt->get_result()->fetch_assoc();
    }

    public function create($nume, $cale) {
        $sql = "INSERT INTO " . $this->table . " (nume, cale) VALUES (?, ?)";
        $stmt = $this->conn->prepare($sql);
        $stmt->bind_param("ss", $nume, $cale);
        return $stmt->execute();
    }

    public function update($id, $nume, $cale) {
        $sql = "UPDATE " . $this->table . " SET nume=?, cale=? WHERE id=?";
        $stmt = $this->conn->prepare($sql);
        $stmt->bind_param("ssi", $nume, $cale, $id);
        return $stmt->execute();
    }

    public function delete($id) {
        $sql = "DELETE FROM " . $this->table . " WHERE id=?";
        $stmt = $this->conn->prepare($sql);
        $stmt->bind_param("i", $id);
        return $stmt->execute();
    }
}
?>
