<?php
class User {
    private $conn;
    private $table = 'utilizatori';

    public function __construct($db) {
        $this->conn = $db;
    }

    public function authenticate($username, $password) {
        $sql = "SELECT * FROM " . $this->table . " WHERE username=?";
        $stmt = $this->conn->prepare($sql);
        $stmt->bind_param("s", $username);
        $stmt->execute();
        $result = $stmt->get_result();
        if ($result->num_rows > 0) {
            $user = $result->fetch_assoc();
            if (password_verify($password, $user['password'])) {
                return $user;
            }
        }
        return false;
    }

    public function setRememberToken($userId, $token) {
        $sql = "UPDATE " . $this->table . " SET remember_token=? WHERE id=?";
        $stmt = $this->conn->prepare($sql);
        $stmt->bind_param("si", $token, $userId);
        return $stmt->execute();
    }

    public function getUserByRememberToken($token) {
        $sql = "SELECT * FROM " . $this->table . " WHERE remember_token=?";
        $stmt = $this->conn->prepare($sql);
        $stmt->bind_param("s", $token);
        $stmt->execute();
        $result = $stmt->get_result();
        if ($result->num_rows > 0) {
            return $result->fetch_assoc();
        }
        return false;
    }
}
?>
