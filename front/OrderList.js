import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Spinner, Alert } from 'react-bootstrap';
import api from './api';
import './styles.css';

const OrderList = () => {
    const [orders, setOrders] = useState([]);
    const [barbers, setBarbers] = useState([]);
    const [offerings, setOfferings] = useState([]);
    const [locations, setLocations] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        setLoading(true);
        try {
            const ordersResponse = await api.get('/orders');
            console.log('Fetched orders:', ordersResponse.data);
            const validOrders = ordersResponse.data.filter(order => order.orderId && order.orderId > 0);
            if (validOrders.length < ordersResponse.data.length) {
                console.warn('Some orders have invalid orderId:', ordersResponse.data);
                setError('Некоторые заказы имеют некорректные данные и были исключены.');
            }
            setOrders(validOrders);

            const barbersResponse = await api.get('/barbers');
            console.log('Fetched barbers:', barbersResponse.data);
            setBarbers(barbersResponse.data);

            const offeringsResponse = await api.get('/offerings');
            console.log('Fetched offerings:', offeringsResponse.data);
            setOfferings(offeringsResponse.data);

            const locationsResponse = await api.get('/locations');
            console.log('Fetched locations:', locationsResponse.data);
            setLocations(locationsResponse.data);

            if (!validOrders.length) {
                setError('Нет доступных заказов для отображения.');
            }
        } catch (err) {
            console.error('Ошибка при загрузке данных:', err.response?.data || err.message);
            setError('Не удалось загрузить данные: ' + (err.response?.data?.message || err.message));
        }
        setLoading(false);
    };

    const handleDelete = async (orderId) => {
        if (!orderId || orderId <= 0) {
            console.error('Invalid orderId for deletion:', orderId);
            setError('Неверный ID заказа.');
            return;
        }
        if (!window.confirm('Вы уверены, что хотите удалить заказ?')) return;
        setLoading(true);
        try {
            console.log('Deleting order with orderId:', orderId);
            await api.delete(`/orders/${orderId}`);
            fetchData();
            setError('');
        } catch (err) {
            console.error('Ошибка при удалении заказа:', err.response?.data || err.message);
            setError('Не удалось удалить заказ: ' + (err.response?.data?.message || err.message));
        }
        setLoading(false);
    };

    const formatDateTime = (dateTime) => {
        if (!dateTime) return 'Не указано';
        const date = new Date(dateTime);
        return date.toLocaleString('ru-RU', {
            day: 'numeric',
            month: 'long',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const getBarberName = (barberId) => {
        const barber = barbers.find(b => b.barberId === barberId);
        return barber ? barber.name : 'Неизвестный барбер';
    };

    const getOfferingName = (offeringId) => {
        const offering = offerings.find(o => o.offeringId === offeringId);
        return offering ? offering.name : 'Неизвестная услуга';
    };

    const getLocationName = (locationId) => {
        const location = locations.find(l => l.locationId === locationId);
        return location ? location.name : 'Неизвестное местоположение';
    };

    return (
        <Container className="py-5">
            <h2 className="text-center mb-4">Список заказов</h2>
            {error && <Alert variant="danger">{error}</Alert>}
            {loading ? (
                <div className="text-center"><Spinner animation="border" /></div>
            ) : (
                <>
                    {orders.length > 0 ? (
                        <Row xs={1} md={2} lg={3} className="g-4">
                            {orders.map(order => (
                                <Col key={order.orderId}>
                                    <Card className="card-item">
                                        <Card.Body>
                                            <Card.Title>Заказ #{order.orderId}</Card.Title>
                                            <Card.Text>
                                                <strong>Барбер:</strong> {getBarberName(order.barberId)}<br />
                                                <strong>Услуга:</strong> {getOfferingName(order.offeringId)}<br />
                                                <strong>Местоположение:</strong> {getLocationName(order.locationId)}<br />
                                                <strong>Дата и время:</strong> {formatDateTime(order.orderDate || order.startTime)}
                                            </Card.Text>
                                            <Button
                                                className="btn-delete"
                                                onClick={() => handleDelete(order.orderId)}
                                                disabled={loading || !order.orderId}
                                            >
                                                Удалить
                                            </Button>
                                        </Card.Body>
                                    </Card>
                                </Col>
                            ))}
                        </Row>
                    ) : (
                        <Alert variant="info">Нет заказов для отображения.</Alert>
                    )}
                </>
            )}
        </Container>
    );
};

export default OrderList;