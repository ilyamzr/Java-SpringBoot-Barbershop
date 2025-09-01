import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Form, Spinner, Alert } from 'react-bootstrap';
import api from './api';
import './styles.css';

const LocationList = () => {
    const [locations, setLocations] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [newLocation, setNewLocation] = useState({ name: '', address: '' });
    const [editingLocation, setEditingLocation] = useState(null);

    useEffect(() => {
        fetchLocations();
    }, []);

    const fetchLocations = async () => {
        setLoading(true);
        try {
            const response = await api.get('/locations');
            console.log('Fetched locations:', response.data); // Отладка
            const validLocations = response.data.filter(location => location.locationId && location.locationId > 0);
            if (validLocations.length < response.data.length) {
                console.warn('Some locations have invalid locationId:', response.data);
                setError('Некоторые локации имеют некорректные данные и были исключены.');
            }
            setLocations(validLocations);
            if (!validLocations.length) {
                setError('Нет доступных локаций для отображения.');
            }
        } catch (err) {
            console.error('Ошибка при загрузке локаций:', err.response?.data || err.message);
            setError('Не удалось загрузить локации.');
        }
        setLoading(false);
    };

    const handleCreate = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const payload = {
                name: newLocation.name,
                address: newLocation.address,
            };
            console.log('Creating location:', payload);
            await api.post('/locations', payload);
            setNewLocation({ name: '', address: '' });
            fetchLocations();
            setError('');
        } catch (err) {
            console.error('Ошибка при создании локации:', err.response?.data || err.message);
            setError('Не удалось создать локацию: ' + (err.response?.data?.message || err.message));
        }
        setLoading(false);
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const payload = {
                name: editingLocation.name,
                address: editingLocation.address,
            };
            console.log('Updating location:', payload);
            await api.put(`/locations/${editingLocation.locationId}`, payload);
            setEditingLocation(null);
            fetchLocations();
            setError('');
        } catch (err) {
            console.error('Ошибка при обновлении локации:', err.response?.data || err.message);
            setError('Не удалось обновить локацию: ' + (err.response?.data?.message || err.message));
        }
        setLoading(false);
    };

    const handleDelete = async (locationId) => {
        if (!locationId || locationId <= 0) {
            console.error('Invalid locationId for deletion:', locationId);
            setError('Неверный ID локации.');
            return;
        }
        if (!window.confirm('Вы уверены, что хотите удалить локацию?')) return;
        setLoading(true);
        try {
            console.log('Deleting location with locationId:', locationId); // Отладка
            await api.delete(`/locations/${locationId}`);
            fetchLocations();
            setError('');
        } catch (err) {
            console.error('Ошибка при удалении локации:', err.response?.data || err.message);
            setError('Не удалось удалить локацию: ' + (err.response?.data?.message || err.message));
        }
        setLoading(false);
    };

    return (
        <Container className="py-5">
            <h2 className="text-center mb-4">Список локаций</h2>
            {error && <Alert variant="danger">{error}</Alert>}
            {loading ? (
                <div className="text-center"><Spinner animation="border" /></div>
            ) : (
                <>
                    <Form onSubmit={handleCreate} className="mb-5">
                        <Row>
                            <Col md={6}>
                                <Form.Group>
                                    <Form.Label>Название</Form.Label>
                                    <Form.Control
                                        type="text"
                                        value={newLocation.name}
                                        onChange={(e) => setNewLocation({ ...newLocation, name: e.target.value })}
                                        required
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group>
                                    <Form.Label>Адрес</Form.Label>
                                    <Form.Control
                                        type="text"
                                        value={newLocation.address}
                                        onChange={(e) => setNewLocation({ ...newLocation, address: e.target.value })}
                                        required
                                    />
                                </Form.Group>
                            </Col>
                        </Row>
                        <Button variant="primary" className="btn-custom mt-3" type="submit" disabled={loading}>
                            Создать
                        </Button>
                    </Form>

                    {locations.length > 0 ? (
                        <Row xs={1} md={2} lg={3} className="g-4">
                            {locations.map(location => (
                                <Col key={location.locationId}>
                                    <Card className="card-item">
                                        <Card.Body>
                                            {editingLocation && editingLocation.locationId === location.locationId ? (
                                                <Form onSubmit={handleUpdate}>
                                                    <Form.Group className="mb-2">
                                                        <Form.Label>Название</Form.Label>
                                                        <Form.Control
                                                            type="text"
                                                            value={editingLocation.name}
                                                            onChange={(e) => setEditingLocation({ ...editingLocation, name: e.target.value })}
                                                            required
                                                        />
                                                    </Form.Group>
                                                    <Form.Group className="mb-2">
                                                        <Form.Label>Адрес</Form.Label>
                                                        <Form.Control
                                                            type="text"
                                                            value={editingLocation.address}
                                                            onChange={(e) => setEditingLocation({ ...editingLocation, address: e.target.value })}
                                                            required
                                                        />
                                                    </Form.Group>
                                                    <Button variant="primary" className="btn-custom me-2" type="submit" disabled={loading}>
                                                        Сохранить
                                                    </Button>
                                                    <Button variant="secondary" onClick={() => setEditingLocation(null)} disabled={loading}>
                                                        Отмена
                                                    </Button>
                                                </Form>
                                            ) : (
                                                <>
                                                    <Card.Title>{location.name}</Card.Title>
                                                    <Card.Text>
                                                        <strong>Адрес:</strong> {location.address}
                                                    </Card.Text>
                                                    <Button
                                                        className="btn-custom me-2"
                                                        onClick={() => setEditingLocation(location)}
                                                        disabled={loading}
                                                    >
                                                        Изменить
                                                    </Button>
                                                    <Button
                                                        className="btn-delete"
                                                        onClick={() => handleDelete(location.locationId)}
                                                        disabled={loading || !location.locationId}
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
                        <Alert variant="info">Нет локаций для отображения.</Alert>
                    )}
                </>
            )}
        </Container>
    );
};

export default LocationList;