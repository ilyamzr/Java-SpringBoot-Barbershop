import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Row, Col, Button, Spinner, Alert, Form } from 'react-bootstrap';
import api from './api';
import './styles.css';

const TimeSelection = () => {
    const { barberId, offeringId } = useParams();
    const [availability, setAvailability] = useState([]);
    const [selectedDate, setSelectedDate] = useState('');
    const [selectedTime, setSelectedTime] = useState('');
    const [barber, setBarber] = useState(null);
    const [offering, setOffering] = useState(null);
    const [locationId, setLocationId] = useState(localStorage.getItem('locationId') || '');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    console.log('Params:', { barberId, offeringId, locationId });

    useEffect(() => {
        if (!locationId) {
            setError('Местоположение не выбрано. Пожалуйста, выберите локацию.');
            navigate('/book/location');
            return;
        }
        fetchBarber();
        if (offeringId) {
            fetchOffering();
        } else {
            setError('ID услуги не указан. Пожалуйста, выберите услугу.');
            navigate(`/book/offerings/${barberId}`);
        }
        fetchAvailability();
    }, [barberId, offeringId, locationId, navigate]);

    const fetchBarber = async () => {
        setLoading(true);
        setError('');
        console.log('Starting fetchBarber for barberId:', barberId);
        try {
            const numericBarberId = parseInt(barberId);
            if (isNaN(numericBarberId)) {
                throw new Error('Неверный ID барбера');
            }
            const response = await api.get(`/barbers/${numericBarberId}`);
            console.log('Fetched barber:', response.data);
            setBarber(response.data);
        } catch (err) {
            console.error('Ошибка при загрузке барбера:', err.response?.data || err.message);
            setError('Не удалось загрузить барбера: ' + (err.response?.data?.message || err.message));
        }
        setLoading(false);
    };

    const fetchOffering = async () => {
        setLoading(true);
        setError('');
        console.log('Starting fetchOffering for offeringId:', offeringId);
        try {
            const numericOfferingId = parseInt(offeringId);
            if (isNaN(numericOfferingId)) {
                throw new Error('Неверный ID услуги');
            }
            const response = await api.get(`/offerings/${numericOfferingId}`);
            console.log('Fetched offering:', response.data);
            setOffering(response.data);
        } catch (err) {
            console.error('Ошибка при загрузке услуги:', err.response?.data || err.message);
            setError('Не удалось загрузить услугу: ' + (err.response?.data?.message || err.message));
        }
        setLoading(false);
    };

    const fetchAvailability = async () => {
        setLoading(true);
        setError('');
        console.log('Starting fetchAvailability for barberId:', barberId);
        try {
            const numericBarberId = parseInt(barberId);
            if (isNaN(numericBarberId)) {
                throw new Error('Неверный ID барбера');
            }
            const response = await api.get(`/barbers/${numericBarberId}/availability`);
            console.log('Fetched availability:', response.data);
            if (!Array.isArray(response.data) || response.data.length === 0) {
                setError('Нет доступного времени для этого барбера.');
            } else {
                setAvailability(response.data);
                setSelectedDate(response.data[0]?.date || '');
            }
        } catch (err) {
            console.error('Ошибка при загрузке доступного времени:', err.response?.data || err.message);
            setError('Не удалось загрузить доступное время: ' + (err.response?.data?.message || err.message));
        }
        setLoading(false);
    };

    const handleSelectTime = (time) => {
        setSelectedTime(time);
    };

    const handleSelectDate = (date) => {
        setSelectedDate(date);
        setSelectedTime('');
    };

    const handleCreateOrder = async () => {
        if (!selectedDate || !selectedTime || !locationId) {
            setError('Пожалуйста, выберите дату, время и местоположение.');
            return;
        }

        setLoading(true);
        setError('');
        console.log('Creating order:', { selectedDate, selectedTime, barberId, offeringId, locationId });
        try {
            const numericBarberId = parseInt(barberId);
            const numericOfferingId = parseInt(offeringId);
            const numericLocationId = parseInt(locationId);
            if (isNaN(numericBarberId) || isNaN(numericOfferingId) || isNaN(numericLocationId)) {
                throw new Error('Неверные ID барбера, услуги или местоположения');
            }

            const storedUser = JSON.parse(localStorage.getItem('user'));
            const userId = storedUser ? storedUser.userId : null;
            if (!userId) {
                setError('Пожалуйста, войдите в аккаунт для создания заказа.');
                navigate('/profile');
                return;
            }

            const orderDate = `${selectedDate}T${selectedTime}:00`;
            const orderData = {
                orderDate,
                barberId: numericBarberId,
                offeringId: numericOfferingId,
                locationId: numericLocationId,
                userId
            };

            console.log('Sending order:', orderData);
            const response = await api.post('/orders', orderData);
            console.log('Order created:', response.data);
            navigate('/orders');
        } catch (err) {
            console.error('Ошибка при создании заказа:', err.response?.data || err.message);
            setError('Не удалось создать заказ: ' + (err.response?.data?.message || err.message));
        }
        setLoading(false);
    };

    return (
        <Container className="py-5">
            <h2 className="text-center mb-4">
                Выберите время для {barber ? barber.name : 'барбера'} - {offering ? offering.name : 'услуги'}
            </h2>
            {error && <Alert variant="danger">{error}</Alert>}
            {loading ? (
                <div className="text-center">
                    <Spinner animation="border" />
                </div>
            ) : availability.length > 0 ? (
                <>
                    <Row className="mb-4">
                        <Col>
                            <Form.Group>
                                <Form.Label>Выберите дату</Form.Label>
                                <div className="d-flex flex-wrap">
                                    {availability.map((avail, index) => (
                                        <Button
                                            key={avail.date}
                                            variant={selectedDate === avail.date ? 'primary' : 'outline-primary'}
                                            className="m-1"
                                            onClick={() => handleSelectDate(avail.date)}
                                        >
                                            {new Date(avail.date).toLocaleDateString('ru-RU', {
                                                day: 'numeric',
                                                month: 'short'
                                            })}
                                        </Button>
                                    ))}
                                </div>
                            </Form.Group>
                        </Col>
                    </Row>
                    {selectedDate && (
                        <Row className="mb-4">
                            <Col>
                                <Form.Group>
                                    <Form.Label>Выберите время</Form.Label>
                                    <div className="d-flex flex-wrap">
                                        {availability
                                            .find(avail => avail.date === selectedDate)
                                            ?.times.map(time => (
                                                <Button
                                                    key={time}
                                                    variant={selectedTime === time ? 'primary' : 'outline-primary'}
                                                    className="m-1"
                                                    onClick={() => handleSelectTime(time)}
                                                >
                                                    {time}
                                                </Button>
                                            )) || <Alert variant="info">Нет доступного времени на эту дату.</Alert>}
                                    </div>
                                </Form.Group>
                            </Col>
                        </Row>
                    )}
                    <Row>
                        <Col className="text-center">
                            <Button
                                variant="primary"
                                className="btn-custom"
                                onClick={handleCreateOrder}
                                disabled={!selectedDate || !selectedTime || !locationId || loading}
                            >
                                Подтвердить запись
                            </Button>
                        </Col>
                    </Row>
                </>
            ) : (
                !error && <Alert variant="info">Нет доступного времени для этого барбера.</Alert>
            )}
        </Container>
    );
};

export default TimeSelection;