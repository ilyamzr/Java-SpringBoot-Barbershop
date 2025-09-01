import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Row, Col, Card, Button, Spinner, Alert } from 'react-bootstrap';
import api from './api';
import './styles.css';

const OfferingSelection = () => {
    const { barberId } = useParams();
    const [offerings, setOfferings] = useState([]);
    const [barber, setBarber] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    console.log('Params:', { barberId });

    useEffect(() => {
        fetchBarber();
        fetchOfferings();
    }, [barberId]);

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

    const fetchOfferings = async () => {
        setLoading(true);
        setError('');
        console.log('Starting fetchOfferings for barberId:', barberId);
        try {
            const numericBarberId = parseInt(barberId);
            if (isNaN(numericBarberId)) {
                throw new Error('Неверный ID барбера');
            }
            let response;
            const url = `/offerings/barber/${numericBarberId}`;
            console.log('Trying Request URL:', url);
            try {
                response = await api.get(url);
            } catch (err) {
                if (err.response?.status === 404) {
                    console.log('Falling back to alternative path');
                    const fallbackUrl = `/api/offerings/barber/${numericBarberId}`;
                    console.log('Trying Fallback URL:', fallbackUrl);
                    response = await api.get(fallbackUrl);
                } else {
                    throw err;
                }
            }
            console.log('Fetched offerings:', response.data);
            if (!Array.isArray(response.data) || response.data.length === 0) {
                setError('Нет доступных услуг для этого барбера.');
            } else {
                response.data.forEach(offering => {
                    console.log('Offering data:', offering);
                    if (!offering.offeringId) {
                        console.warn('Offering missing offeringId:', offering);
                    }
                });
                setOfferings(response.data);
            }
        } catch (err) {
            console.error('Ошибка при загрузке услуг:', err.response?.data || err.message);
            setError('Не удалось загрузить услуги: ' + (err.response?.data?.message || err.message));
        }
        setLoading(false);
    };

    const handleSelectOffering = (offeringId) => {
        if (!offeringId) {
            console.error('handleSelectOffering: offeringId is undefined');
            setError('Ошибка: ID услуги не определен.');
            return;
        }
        console.log('Selected offeringId:', offeringId);
        navigate(`/book/time/${barberId}/${offeringId}`);
    };

    return (
        <Container className="py-5">
            <h2 className="text-center mb-4">
                Выберите услугу для {barber ? barber.name : 'барбера'}
            </h2>
            {error && <Alert variant="danger">{error}</Alert>}
            {loading ? (
                <div className="text-center">
                    <Spinner animation="border" />
                </div>
            ) : offerings.length > 0 ? (
                <Row xs={1} md={2} lg={3} className="g-4">
                    {offerings.map(offering => (
                        <Col key={offering.offeringId || offering.name}>
                            <Card className="card-item">
                                <Card.Body>
                                    <Card.Title>{offering.name}</Card.Title>
                                    <Card.Text>
                                        <strong>Цена:</strong> {offering.price} руб.<br />
                                        <strong>Длительность:</strong> {offering.duration} минут
                                    </Card.Text>
                                    <Button
                                        variant="primary"
                                        className="btn-custom"
                                        onClick={() => {
                                            console.log('Selecting offering:', offering);
                                            handleSelectOffering(offering.offeringId);
                                        }}
                                    >
                                        Выбрать
                                    </Button>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>
            ) : (
                !error && <Alert variant="info">Нет доступных услуг для этого барбера.</Alert>
            )}
        </Container>
    );
};

export default OfferingSelection;