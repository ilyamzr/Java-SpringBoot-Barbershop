import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Form, Spinner, Alert } from 'react-bootstrap';
import api from './api';
import './styles.css';

const Profile = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [isRegistering, setIsRegistering] = useState(false);
    const [formData, setFormData] = useState({ username: '', password: '' });

    useEffect(() => {
        const storedUser = JSON.parse(localStorage.getItem('user'));
        if (storedUser) {
            setUser(storedUser);
            setIsLoggedIn(true);
        }
    }, []);

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        try {
            // Попытка "логина" через создание пользователя или проверку
            const response = await api.post('/users', { username: formData.username, password: formData.password });
            const loggedInUser = response.data;
            localStorage.setItem('user', JSON.stringify(loggedInUser));
            setUser(loggedInUser);
            setIsLoggedIn(true);
            setFormData({ username: '', password: '' });
            console.log('Успешный вход:', loggedInUser);
        } catch (err) {
            console.error('Ошибка при входе:', err.response?.data || err.message);
            setError('Не удалось войти: ' + (err.response?.data?.message || err.message));
        }
        setLoading(false);
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        try {
            const response = await api.post('/users', { username: formData.username, password: formData.password });
            const newUser = response.data;
            localStorage.setItem('user', JSON.stringify(newUser));
            setUser(newUser);
            setIsLoggedIn(true);
            setFormData({ username: '', password: '' });
            console.log('Успешная регистрация:', newUser);
        } catch (err) {
            console.error('Ошибка при регистрации:', err.response?.data || err.message);
            setError('Не удалось зарегистрироваться: ' + (err.response?.data?.message || err.message));
        }
        setLoading(false);
    };

    const handleLogout = () => {
        localStorage.removeItem('user');
        setUser(null);
        setIsLoggedIn(false);
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    return (
        <Container className="py-5 d-flex justify-content-center align-items-center profile-container">
            <div className="text-center">
                <h2 className="mb-4 profile-title">Ваш профиль</h2>
                {error && <Alert variant="danger" className="profile-text">{error}</Alert>}
                {loading ? (
                    <div className="text-center">
                        <Spinner animation="border" />
                    </div>
                ) : isLoggedIn ? (
                    <Card className="card-item mx-auto" style={{ maxWidth: '400px' }}>
                        <Card.Body>
                            <Card.Title className="profile-title">{user.username}</Card.Title>
                            <Card.Text className="profile-text">
                                <strong>Имя пользователя:</strong> {user.username}
                            </Card.Text>
                            <Button variant="primary" className="btn-custom" onClick={handleLogout}>
                                Выйти
                            </Button>
                        </Card.Body>
                    </Card>
                ) : (
                    <Row className="justify-content-center">
                        <Col md={6} className="profile-form">
                            <h3 className="profile-subtitle">{isRegistering ? 'Регистрация' : 'Вход'}</h3>
                            <Form onSubmit={isRegistering ? handleRegister : handleLogin}>
                                <Form.Group className="mb-3">
                                    <Form.Label className="profile-text">Имя пользователя</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="username"
                                        value={formData.username}
                                        onChange={handleInputChange}
                                        required
                                        className="profile-input"
                                    />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label className="profile-text">Пароль</Form.Label>
                                    <Form.Control
                                        type="password"
                                        name="password"
                                        value={formData.password}
                                        onChange={handleInputChange}
                                        required
                                        className="profile-input"
                                    />
                                </Form.Group>
                                <Button variant="primary" className="btn-custom" type="submit" disabled={loading}>
                                    {isRegistering ? 'Зарегистрироваться' : 'Войти'}
                                </Button>
                            </Form>
                            <Button
                                variant="link"
                                onClick={() => setIsRegistering(!isRegistering)}
                                className="mt-3 profile-text"
                            >
                                {isRegistering ? 'Уже есть аккаунт? Войти' : 'Нет аккаунта? Зарегистрироваться'}
                            </Button>
                        </Col>
                    </Row>
                )}
            </div>
        </Container>
    );
};

export default Profile;