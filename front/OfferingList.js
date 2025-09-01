import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Form, Spinner, Alert } from 'react-bootstrap';
import api from './api';
import './styles.css';

const OfferingList = () => {
    const [offerings, setOfferings] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [newOffering, setNewOffering] = useState({ name: '', price: '', duration: '' });
    const [editingOffering, setEditingOffering] = useState(null);

    useEffect(() => {
        fetchOfferings();
    }, []);

    const fetchOfferings = async () => {
        setLoading(true);
        try {
            const response = await api.get('/offerings');
            console.log('Fetched offerings:', response.data); // Отладка
            // Фильтруем услуги с валидным offeringId
            const validOfferings = response.data.filter(offering => offering.offeringId && offering.offeringId > 0);
            if (validOfferings.length < response.data.length) {
                console.warn('Some offerings have invalid offeringId:', response.data);
                setError('Некоторые услуги имеют некорректные данные и были исключены.');
            }
            setOfferings(validOfferings);
            if (!validOfferings.length) {
                setError('Нет доступных услуг для отображения.');
            }
        } catch (err) {
            console.error('Ошибка при загрузке услуг:', err.response?.data || err.message);
            setError('Не удалось загрузить услуги.');
        }
        setLoading(false);
    };

    const handleCreate = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const payload = {
                name: newOffering.name,
                price: parseFloat(newOffering.price),
                duration: parseInt(newOffering.duration),
            };
            console.log('Creating offering:', payload); // Отладка
            await api.post('/offerings', payload);
            setNewOffering({ name: '', price: '', duration: '' });
            fetchOfferings();
            setError('');
        } catch (err) {
            console.error('Ошибка при создании услуги:', err.response?.data || err.message);
            setError('Не удалось создать услугу: ' + (err.response?.data?.message || err.message));
        }
        setLoading(false);
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const payload = {
                name: editingOffering.name,
                price: parseFloat(editingOffering.price),
                duration: parseInt(editingOffering.duration),
            };
            console.log('Updating offering:', payload); // Отладка
            await api.put(`/offerings/${editingOffering.offeringId}`, payload);
            setEditingOffering(null);
            fetchOfferings();
            setError('');
        } catch (err) {
            console.error('Ошибка при обновлении услуги:', err.response?.data || err.message);
            setError('Не удалось обновить услугу: ' + (err.response?.data?.message || err.message));
        }
        setLoading(false);
    };

    const handleDelete = async (offeringId) => {
        if (!offeringId || offeringId <= 0) {
            console.error('Invalid offeringId for deletion:', offeringId);
            setError('Неверный ID услуги.');
            return;
        }
        if (!window.confirm('Вы уверены, что хотите удалить услугу?')) return;
        setLoading(true);
        try {
            console.log('Deleting offering with offeringId:', offeringId); // Отладка
            await api.delete(`/offerings/${offeringId}`);
            fetchOfferings();
            setError('');
        } catch (err) {
            console.error('Ошибка при удалении услуги:', err.response?.data || err.message);
            setError('Не удалось удалить услугу: ' + (err.response?.data?.message || err.message));
        }
        setLoading(false);
    };

    return (
        <Container className="py-5">
            <h2 className="text-center mb-4">Список услуг</h2>
            {error && <Alert variant="danger">{error}</Alert>}
            {loading ? (
                <div className="text-center"><Spinner animation="border" /></div>
            ) : (
                <>
                    <Form onSubmit={handleCreate} className="mb-5">
                        <Row>
                            <Col md={4}>
                                <Form.Group>
                                    <Form.Label>Название</Form.Label>
                                    <Form.Control
                                        type="text"
                                        value={newOffering.name}
                                        onChange={(e) => setNewOffering({ ...newOffering, name: e.target.value })}
                                        required
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={4}>
                                <Form.Group>
                                    <Form.Label>Цена (₽)</Form.Label>
                                    <Form.Control
                                        type="number"
                                        step="0.01"
                                        value={newOffering.price}
                                        onChange={(e) => setNewOffering({ ...newOffering, price: e.target.value })}
                                        required
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={4}>
                                <Form.Group>
                                    <Form.Label>Длительность (мин)</Form.Label>
                                    <Form.Control
                                        type="number"
                                        value={newOffering.duration}
                                        onChange={(e) => setNewOffering({ ...newOffering, duration: e.target.value })}
                                        required
                                    />
                                </Form.Group>
                            </Col>
                        </Row>
                        <Button variant="primary" className="btn-custom mt-3" type="submit" disabled={loading}>
                            Создать
                        </Button>
                    </Form>

                    {/* Список услуг */}
                    {offerings.length > 0 ? (
                        <Row xs={1} md={2} lg={3} className="g-4">
                            {offerings.map(offering => (
                                <Col key={offering.offeringId}>
                                    <Card className="card-item">
                                        <Card.Body>
                                            {editingOffering && editingOffering.offeringId === offering.offeringId ? (
                                                <Form onSubmit={handleUpdate}>
                                                    <Form.Group className="mb-2">
                                                        <Form.Label>Название</Form.Label>
                                                        <Form.Control
                                                            type="text"
                                                            value={editingOffering.name}
                                                            onChange={(e) => setEditingOffering({ ...editingOffering, name: e.target.value })}
                                                            required
                                                        />
                                                    </Form.Group>
                                                    <Form.Group className="mb-2">
                                                        <Form.Label>Цена (₽)</Form.Label>
                                                        <Form.Control
                                                            type="number"
                                                            step="0.01"
                                                            value={editingOffering.price}
                                                            onChange={(e) => setEditingOffering({ ...editingOffering, price: e.target.value })}
                                                            required
                                                        />
                                                    </Form.Group>
                                                    <Form.Group className="mb-2">
                                                        <Form.Label>Длительность (мин)</Form.Label>
                                                        <Form.Control
                                                            type="number"
                                                            value={editingOffering.duration}
                                                            onChange={(e) => setEditingOffering({ ...editingOffering, duration: e.target.value })}
                                                            required
                                                        />
                                                    </Form.Group>
                                                    <Button variant="primary" className="btn-custom me-2" type="submit" disabled={loading}>
                                                        Сохранить
                                                    </Button>
                                                    <Button variant="secondary" onClick={() => setEditingOffering(null)} disabled={loading}>
                                                        Отмена
                                                    </Button>
                                                </Form>
                                            ) : (
                                                <>
                                                    <Card.Title>{offering.name}</Card.Title>
                                                    <Card.Text>
                                                        <strong>Цена:</strong> {offering.price} ₽<br />
                                                        <strong>Длительность:</strong> {offering.duration} мин
                                                    </Card.Text>
                                                    <Button
                                                        className="btn-custom me-2"
                                                        onClick={() => setEditingOffering(offering)}
                                                        disabled={loading}
                                                    >
                                                        Изменить
                                                    </Button>
                                                    <Button
                                                        className="btn-delete"
                                                        onClick={() => handleDelete(offering.offeringId)}
                                                        disabled={loading || !offering.offeringId}
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
                        <Alert variant="info">Нет услуг для отображения.</Alert>
                    )}
                </>
            )}
        </Container>
    );
};

export default OfferingList;