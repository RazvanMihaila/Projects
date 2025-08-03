<?php
class Haine {
    private $mysqli;

    public function __construct($mysqli) {
        $this->mysqli = $mysqli;
    }

    public function getAll() {
        $stmt = $this->mysqli->prepare("SELECT * FROM haine");
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_all(MYSQLI_ASSOC);
    }

    public function getById($id) {
        $stmt = $this->mysqli->prepare("SELECT * FROM haine WHERE id = ?");
        $stmt->bind_param('i', $id);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result->fetch_assoc();
    }

    public function create($tip, $material, $culoare, $marime, $pret, $imagine) {
        $stmt = $this->mysqli->prepare("INSERT INTO haine (tip, material, culoare, marime, pret, imagine) VALUES (?, ?, ?, ?, ?, ?)");
        $stmt->bind_param('ssssds', $tip, $material, $culoare, $marime, $pret, $imagine);
        $stmt->execute();
    }

    public function update($id, $tip, $material, $culoare, $marime, $pret, $imagine) {
        $stmt = $this->mysqli->prepare("UPDATE haine SET tip = ?, material = ?, culoare = ?, marime = ?, pret = ?, imagine = ? WHERE id = ?");
        $stmt->bind_param('ssssdsi', $tip, $material, $culoare, $marime, $pret, $imagine, $id);
        $stmt->execute();
    }

    public function delete($id) {
        $stmt = $this->mysqli->prepare("DELETE FROM haine WHERE id = ?");
        $stmt->bind_param('i', $id);
        $stmt->execute();
    }
}


