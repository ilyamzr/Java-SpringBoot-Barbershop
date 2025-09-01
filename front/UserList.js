import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Form, Spinner, Alert } from 'react-bootstrap';
import api from './api';
import './styles.css';

const UserList = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [editingUser, setEditingUser] = useState(null);

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        setLoading(true);
        try {
            const response = await api.get('/users');
            console.log('Fetched users:', response.data); // Debugging
            const validUsers = response.data.filter(user => user.userId && user.userId > 0);
            if (validUsers.length < response.data.length) {
                console.warn('Some users have invalid userId:', response.data);
                setError('Некоторые пользователи имеют некорректные данные и были исключены.');
            }
            setUsers(validUsers);
            if (!validUsers.length) {
                setError('Нет доступных пользователей для отображения.');
            }
        } catch (err) {
            console.error('Ошибка при загрузке пользователей:', err.response?.data || err.message);
            setError('Не удалось загрузить пользователей.');
        }
        setLoading(false);
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const payload = {
                username: editingUser.username,
                password: editingUser.password || '', // Пароль опционален
            };
            console.log('Updating user:', payload); // Debugging
            await api.put(`/users/${editingUser.userId}`, payload);
            setEditingUser(null);
            fetchUsers();
            setError('');
        } catch (err) {
            console.error('Ошибка при обновлении пользователя:', err.response?.data || err.message);
            setError('Не удалось обновить пользователя: ' + (err.response?.data?.message || err.message));
        }
        setLoading(false);
    };

    const handleDelete = async (userId) => {
        if (!userId || userId <= 0) {
            console.error('Invalid userId for deletion:', userId);
            setError('Неверный ID пользователя.');
            return;
        }
        if (!window.confirm('Вы уверены, что хотите удалить пользователя?')) return;
        setLoading(true);
        try {
            console.log('Deleting user with userId:', userId); // Debugging
            await api.delete(`/users/${userId}`);
            fetchUsers();
            setError('');
        } catch (err) {
            console.error('Ошибка при удалении пользователя:', err.response?.data || err.message);
            setError('Не удалось удалить пользователя: ' + (err.response?.data?.message || err.message));
        }
        setLoading(false);
    };

    return (
        <Container className="py-5">
            <h2 className="text-center mb-4">Список пользователей</h2>
            {error && <Alert variant="danger">{error}</Alert>}
            {loading ? (
                <div className="text-center"><Spinner animation="border" /></div>
            ) : (
                <>
                    {users.length > 0 ? (
                        <Row xs={1} md={2} lg={3} className="g-4">
                            {users.map(user => (
                                <Col key={user.userId}>
                                    <Card className="card-item">
                                        <Card.Body>
                                            {editingUser && editingUser.userId === user.userId ? (
                                                <Form onSubmit={handleUpdate}>
                                                    <Form.Group className="mb-2">
                                                        <Form.Label>Имя пользователя</Form.Label>
                                                        <Form.Control
                                                            type="text"
                                                            value={editingUser.username}
                                                            onChange={(e) => setEditingUser({ ...editingUser, username: e.target.value })}
                                                            required
                                                        />
                                                    </Form.Group>
                                                    <Form.Group className="mb-2">
                                                        <Form.Label>Пароль</Form.Label>
                                                        <Form.Control
                                                            type="password"
                                                            value={editingUser.password || ''}
                                                            onChange={(e) => setEditingUser({ ...editingUser, password: e.target.value })}
                                                        />
                                                    </Form.Group>
                                                    <Button variant="primary" className="btn-custom me-2" type="submit" disabled={loading}>
                                                        Сохранить
                                                    </Button>
                                                    <Button variant="secondary" onClick={() => setEditingUser(null)} disabled={loading}>
                                                        Отмена
                                                    </Button>
                                                </Form>
                                            ) : (
                                                <>
                                                    <Card.Title>{user.username}</Card.Title>
                                                    <Card.Text>
                                                        <strong>Имя пользователя:</strong> {user.username}<br />
                                                        <strong>Пароль:</strong> {user.password}
                                                    </Card.Text>
                                                    <Button
                                                        className="btn-custom me-2"
                                                        onClick={() => setEditingUser(user)}
                                                        disabled={loading}
                                                    >
                                                        Изменить
                                                    </Button>
                                                    <Button
                                                        className="btn-delete"
                                                        onClick={() => handleDelete(user.userId)}
                                                        disabled={loading || !user.userId}
                                                    >
                                                        Удалить
                                                    </Button>
                                                </>
                                            )}
                                        </Card.Body>
                                    </Card>
                                </Col>
                            ))}
                        </Row>
                    ) : (
                        <Alert variant="info">Нет пользователей для отображения.</Alert>
                    )}
                </>
            )}
        </Container>
    );
};

export default UserList;