import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Form, Spinner, Alert } from 'react-bootstrap';
import api from './api';
import './styles.css';

const BarberList = () => {
    const [barbers, setBarbers] = useState([]);
    const [locations, setLocations] = useState([]);
    const [offerings, setOfferings] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [newBarber, setNewBarber] = useState({
        name: '',
        availableDays: [],
        startTime: '08:00',
        endTime: '18:00',
        locationId: '',
        offeringIds: []
    });
    const [editingBarber, setEditingBarber] = useState(null);
    const [showCreateForm, setShowCreateForm] = useState(false);

    const dayMapping = {
        'Пн': 'MONDAY',
        'Вт': 'TUESDAY',
        'Ср': 'WEDNESDAY',
        'Чт': 'THURSDAY',
        'Пт': 'FRIDAY',
        'Сб': 'SATURDAY',
        'Вс': 'SUNDAY'
    };

    const reverseDayMapping = Object.fromEntries(
        Object.entries(dayMapping).map(([key, value]) => [value, key])
    );

    useEffect(() => {
        fetchBarbers();
        fetchLocations();
        fetchOfferings();
    }, []);

    const fetchBarbers = async () => {
        setLoading(true);
        try {
            const response = await api.get('/barbers');
            console.log('Fetched barbers:', response.data);
            const validBarbers = response.data.filter(barber => barber.barberId && barber.barberId > 0);
            if (validBarbers.length < response.data.length) {
                console.warn('Some barbers have invalid barberId:', response.data);
                setError('Некоторые барберы имеют некорректные данные и были исключены.');
            }
            setBarbers(validBarbers);
            if (!validBarbers.length) {
                setError('Нет доступных барберов для отображения.');
            }
        } catch (err) {
            console.error('Ошибка при загрузке барберов:', err.response?.data || err.message);
            setError('Не удалось загрузить барберов.');
        }
        setLoading(false);
    };

    const fetchLocations = async () => {
        try {
            const response = await api.get('/locations');
            console.log('Fetched locations:', response.data);
            const formattedLocations = Array.isArray(response.data)
                ? response.data.map(loc => {
                    if (typeof loc === 'string') {
                        return { locationId: loc, name: loc };
                    } else if (loc && loc.locationId && loc.name) {
                        return loc;
                    } else {
                        console.warn('Invalid location format:', loc);
                        return null;
                    }
                }).filter(loc => loc !== null)
                : [];
            if (formattedLocations.length === 0) {
                setError('Локации не найдены или имеют неверный формат.');
            }
            setLocations(formattedLocations);
        } catch (err) {
            console.error('Ошибка при загрузке локаций:', err.response?.data || err.message);
            setError('Не удалось загрузить локации.');
        }
    };

    const fetchOfferings = async () => {
        try {
            const response = await api.get('/offerings');
            console.log('Fetched offerings:', response.data);
            setOfferings(response.data);
        } catch (err) {
            console.error('Ошибка при загрузке услуг:', err.response?.data || err.message);
            setError('Не удалось загрузить услуги.');
        }
    };

    const handleCreate = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        if (!newBarber.name.trim()) {
            setError('Имя барбера обязательно.');
            setLoading(false);
            return;
        }
        if (newBarber.availableDays.length === 0) {
            setError('Выберите хотя бы один день.');
            setLoading(false);
            return;
        }
        if (!newBarber.locationId || isNaN(parseInt(newBarber.locationId))) {
            setError('Выберите корректную локацию.');
            setLoading(false);
            return;
        }
        if (newBarber.offeringIds.length === 0) {
            setError('Выберите хотя бы одну услугу.');
            setLoading(false);
            return;
        }

        try {
            const barberPayload = {
                name: newBarber.name.trim(),
                availableDays: newBarber.availableDays.map(day => dayMapping[day]),
                startTime: newBarber.startTime + ':00',
                endTime: newBarber.endTime + ':00',
            };
            console.log('Creating barber:', barberPayload);
            const response = await api.post('/barbers', barberPayload);
            const createdBarber = response.data;
            console.log('Created barber:', createdBarber);

            const locationId = parseInt(newBarber.locationId);
            console.log(`Linking location ${locationId} for barber ${createdBarber.barberId}`);
            await api.post(`/barbers/${createdBarber.barberId}/locations/${locationId}`);

            const offeringPromises = newBarber.offeringIds.map(id => {
                console.log(`Linking offering ${id} for barber ${createdBarber.barberId}`);
                return api.post(`/barbers/${createdBarber.barberId}/offerings/${id}`);
            });
            await Promise.all(offeringPromises);

            setNewBarber({ name: '', availableDays: [], startTime: '08:00', endTime: '18:00', locationId: '', offeringIds: [] });
            setShowCreateForm(false);
            fetchBarbers();
        } catch (err) {
            console.error('Ошибка при создании барбера:', err.response?.data || err.message);
            const errorMessage = err.response?.data?.message ||
                err.response?.data?.error ||
                JSON.stringify(err.response?.data) ||
                err.message;
            setError(`Не удалось создать барбера: ${errorMessage}`);
        }
        setLoading(false);
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        if (!editingBarber.name.trim()) {
            setError('Имя барбера обязательно.');
            setLoading(false);
            return;
        }
        if (editingBarber.availableDays.length === 0) {
            setError('Выберите хотя бы один день.');
            setLoading(false);
            return;
        }

        try {
            const barberPayload = {
                barberId: editingBarber.barberId,
                name: editingBarber.name.trim(),
                availableDays: editingBarber.availableDays.map(day => dayMapping[day]),
                startTime: editingBarber.startTime + ':00',
                endTime: editingBarber.endTime + ':00',
            };
            console.log('Updating barber:', barberPayload);
            await api.put(`/barbers/${editingBarber.barberId}`, barberPayload);

            setEditingBarber(null);
            fetchBarbers();
        } catch (err) {
            console.error('Ошибка при обновлении барбера:', err.response?.data || err.message);
            const errorMessage = err.response?.data?.message ||
                err.response?.data?.error ||
                JSON.stringify(err.response?.data) ||
                err.message;
            setError(`Не удалось обновить барбера: ${errorMessage}`);
        }
        setLoading(false);
    };

    const handleDelete = async (barberId) => {
        if (!barberId || barberId <= 0) {
            console.error('Invalid barberId for deletion:', barberId);
            setError('Неверный ID барбера.');
            return;
        }
        if (!window.confirm('Вы уверены, что хотите удалить барбера?')) return;
        setLoading(true);
        try {
            console.log('Deleting barber with barberId:', barberId);
            await api.delete(`/barbers/${barberId}`);
            fetchBarbers();
            setError('');
        } catch (err) {
            console.error('Ошибка при удалении барбера:', err.response?.data || err.message);
            const errorMessage = err.response?.data?.message ||
                err.response?.data?.error ||
                JSON.stringify(err.response?.data) ||
                err.message;
            setError(`Не удалось удалить барбера: ${errorMessage}`);
        }
        setLoading(false);
    };

    const daysOfWeek = ['Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб', 'Вс'];

    const timeOptions = [];
    for (let hour = 8; hour <= 22; hour++) {
        timeOptions.push(`${hour.toString().padStart(2, '0')}:00`);
        if (hour < 22) timeOptions.push(`${hour.toString().padStart(2, '0')}:30`);
    }

    const getLocationName = (barber) => {
        return barber.locationName || 'Не привязана';
    };

    const toggleDay = (day, isNewBarber) => {
        if (isNewBarber) {
            setNewBarber(prev => ({
                ...prev,
                availableDays: prev.availableDays.includes(day)
                    ? prev.availableDays.filter(d => d !== day)
                    : [...prev.availableDays, day]
            }));
        } else {
            setEditingBarber(prev => ({
                ...prev,
                availableDays: prev.availableDays.includes(day)
                    ? prev.availableDays.filter(d => d !== day)
                    : [...prev.availableDays, day]
            }));
        }
    };

    const toggleOffering = (offeringId) => {
        const id = offeringId.toString();
        console.log(`Toggling offering ID: ${id}`);
        setNewBarber(prev => {
            const newOfferingIds = prev.offeringIds.includes(id)
                ? prev.offeringIds.filter(oid => oid !== id)
                : [...prev.offeringIds, id];
            console.log('New offeringIds:', newOfferingIds);
            return { ...prev, offeringIds: newOfferingIds };
        });
    };

    return (
        <Container className="py-5">
            <h2 className="text-center mb-4">Список барберов</h2>
            {error && <Alert variant="danger">{error}</Alert>}
            {loading ? (
                <div className="text-center"><Spinner animation="border" /></div>
            ) : (
                <>
                    {!showCreateForm && (
                        <Button
                            variant="primary"
                            className="btn-custom mb-4"
                            onClick={() => setShowCreateForm(true)}
                        >
                            Добавить барбера
                        </Button>
                    )}

                    {showCreateForm && (
                        <Form onSubmit={handleCreate} className="mb-5">
                            <Row>
                                <Col md={4}>
                                    <Form.Group>
                                        <Form.Label>Имя</Form.Label>
                                        <Form.Control
                                            type="text"
                                            value={newBarber.name}
                                            onChange={(e) => setNewBarber({ ...newBarber, name: e.target.value })}
                                            required
                                        />
                                    </Form.Group>
                                </Col>
                                <Col md={4}>
                                    <Form.Group>
                                        <Form.Label>Доступные дни</Form.Label>
                                        <div>
                                            {daysOfWeek.map(day => (
                                                <Form.Check
                                                    inline
                                                    key={day}
                                                    label={day}
                                                    type="checkbox"
                                                    checked={newBarber.availableDays.includes(day)}
                                                    onChange={() => toggleDay(day, true)}
                                                />
                                            ))}
                                        </div>
                                    </Form.Group>
                                </Col>
                                <Col md={2}>
                                    <Form.Group>
                                        <Form.Label>Время начала</Form.Label>
                                        <Form.Select
                                            value={newBarber.startTime}
                                            onChange={(e) => setNewBarber({ ...newBarber, startTime: e.target.value })}
                                            required
                                        >
                                            {timeOptions.map(time => (
                                                <option key={time} value={time}>{time}</option>
                                            ))}
                                        </Form.Select>
                                    </Form.Group>
                                </Col>
                                <Col md={2}>
                                    <Form.Group>
                                        <Form.Label>Время окончания</Form.Label>
                                        <Form.Select
                                            value={newBarber.endTime}
                                            onChange={(e) => setNewBarber({ ...newBarber, endTime: e.target.value })}
                                            required
                                        >
                                            {timeOptions.map(time => (
                                                <option key={time} value={time}>{time}</option>
                                            ))}
                                        </Form.Select>
                                    </Form.Group>
                                </Col>
                            </Row>
                            <Row className="mt-3">
                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label>Локация</Form.Label>
                                        {locations.length > 0 ? (
                                            <Form.Select
                                                value={newBarber.locationId}
                                                onChange={(e) => setNewBarber({ ...newBarber, locationId: e.target.value })}
                                                required
                                            >
                                                <option value="">Выберите локацию</option>
                                                {locations.map(loc => (
                                                    <option key={loc.locationId} value={loc.locationId}>{loc.name}</option>
                                                ))}
                                            </Form.Select>
                                        ) : (
                                            <Form.Text className="text-danger">Локации не загружены</Form.Text>
                                        )}
                                    </Form.Group>
                                </Col>
                                <Col md={6}>
                                    <Form.Group>
                                        <Form.Label>Услуги</Form.Label>
                                        <div>
                                            {offerings.length > 0 ? (
                                                offerings.map(offering => (
                                                    <Form.Check
                                                        inline
                                                        key={offering.offeringId}
                                                        label={offering.name}
                                                        type="checkbox"
                                                        checked={newBarber.offeringIds.includes(offering.offeringId.toString())}
                                                        onChange={() => toggleOffering(offering.offeringId)}
                                                    />
                                                ))
                                            ) : (
                                                <Form.Text>Нет доступных услуг</Form.Text>
                                            )}
                                        </div>
                                    </Form.Group>
                                </Col>
                            </Row>
                            <Button variant="primary" className="btn-custom mt-3 me-2" type="submit" disabled={loading}>
                                Создать
                            </Button>
                            <Button
                                variant="secondary"
                                className="mt-3"
                                onClick={() => setShowCreateForm(false)}
                                disabled={loading}
                            >
                                Отмена
                            </Button>
                        </Form>
                    )}

                    {barbers.length > 0 ? (
                        <Row xs={1} md={2} lg={3} className="g-4">
                            {barbers.map(barber => (
                                <Col key={barber.barberId}>
                                    <Card className="card-item uniform-card">
                                        <Card.Body className="d-flex flex-column">
                                            <Card.Title>{barber.name}</Card.Title>
                                            <Card.Text className="flex-grow-1">
                                                <strong>Локация:</strong> {getLocationName(barber)}<br />
                                                <strong>Доступные дни:</strong> {barber.availableDays && barber.availableDays.length > 0
                                                ? barber.availableDays.map(day => reverseDayMapping[day] || day).join(', ')
                                                : 'Не указаны'}<br />
                                                <strong>Время работы:</strong> {barber.startTime} - {barber.endTime}<br />
                                                <strong>Услуги:</strong> {barber.offerings && barber.offerings.length > 0
                                                ? barber.offerings.map(o => o.name).join(', ')
                                                : 'Не привязаны'}
                                            </Card.Text>
                                            <div className="button-group mt-auto">
                                                <Button
                                                    className="btn-custom me-2"
                                                    onClick={() => setEditingBarber({
                                                        ...barber,
                                                        availableDays: barber.availableDays
                                                            ? barber.availableDays.map(day => reverseDayMapping[day] || day)
                                                            : [],
                                                        startTime: barber.startTime.split(':').slice(0, 2).join(':'),
                                                        endTime: barber.endTime.split(':').slice(0, 2).join(':'),
                                                    })}
                                                    disabled={loading}
                                                >
                                                    Изменить
                                                </Button>
                                                <Button
                                                    className="btn-delete"
                                                    onClick={() => handleDelete(barber.barberId)}
                                                    disabled={loading || !barber.barberId}
                                                >
                                                    Удалить
                                                </Button>
                                            </div>
                                        </Card.Body>
                                    </Card>
                                </Col>
                            ))}
                        </Row>
                    ) : (
                        <Alert variant="info">Нет барберов для отображения.</Alert>
                    )}

                    {editingBarber && (
                        <Form onSubmit={handleUpdate} className="mt-5">
                            <Row>
                                <Col md={4}>
                                    <Form.Group>
                                        <Form.Label>Имя</Form.Label>
                                        <Form.Control
                                            type="text"
                                            value={editingBarber.name}
                                            onChange={(e) => setEditingBarber({ ...editingBarber, name: e.target.value })}
                                            required
                                        />
                                    </Form.Group>
                                </Col>
                                <Col md={4}>
                                    <Form.Group>
                                        <Form.Label>Доступные дни</Form.Label>
                                        <div>
                                            {daysOfWeek.map(day => (
                                                <Form.Check
                                                    inline
                                                    key={day}
                                                    label={day}
                                                    type="checkbox"
                                                    checked={editingBarber.availableDays.includes(day)}
                                                    onChange={() => toggleDay(day, false)}
                                                />
                                            ))}
                                        </div>
                                    </Form.Group>
                                </Col>
                                <Col md={2}>
                                    <Form.Group>
                                        <Form.Label>Время начала</Form.Label>
                                        <Form.Select
                                            value={editingBarber.startTime}
                                            onChange={(e) => setEditingBarber({ ...editingBarber, startTime: e.target.value })}
                                            required
                                        >
                                            {timeOptions.map(time => (
                                                <option key={time} value={time}>{time}</option>
                                            ))}
                                        </Form.Select>
                                    </Form.Group>
                                </Col>
                                <Col md={2}>
                                    <Form.Group>
                                        <Form.Label>Время окончания</Form.Label>
                                        <Form.Select
                                            value={editingBarber.endTime}
                                            onChange={(e) => setEditingBarber({ ...editingBarber, endTime: e.target.value })}
                                            required
                                        >
                                            {timeOptions.map(time => (
                                                <option key={time} value={time}>{time}</option>
                                            ))}
                                        </Form.Select>
                                    </Form.Group>
                                </Col>
                            </Row>
                            <Button variant="primary" className="btn-custom mt-3 me-2" type="submit" disabled={loading}>
                                Сохранить
                            </Button>
                            <Button
                                variant="secondary"
                                className="mt-3"
                                onClick={() => setEditingBarber(null)}
                                disabled={loading}
                            >
                                Отмена
                            </Button>
                        </Form>
                    )}
                </>
            )}
        </Container>
    );
};

export default BarberList;