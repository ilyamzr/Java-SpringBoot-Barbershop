import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Row, Col, Card, Button, Alert, Spinner } from 'react-bootstrap';
import api from './api';
import './styles.css';

const BarberSelection = () => {
    const { locationId } = useParams();
    const [barbers, setBarbers] = useState([]);
    const [locationName, setLocationName] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    console.log('Params:', { locationId });

    useEffect(() => {
        fetchLocationName();
    }, [locationId]);

    const fetchLocationName = async () => {
        setLoading(true);
        setError('');
        console.log('Starting fetchLocationName for locationId:', locationId);
        try {
            const numericLocationId = parseInt(locationId);
            if (isNaN(numericLocationId)) {
                throw new Error('Неверный ID локации');
            }
            const response = await api.get(`/locations/${numericLocationId}`);
            console.log('Fetched location:', response.data);
            if (!response.data || !response.data.name) {
                throw new Error('Локация не найдена');
            }
            setLocationName(response.data.name);
        } catch (err) {
            console.error('Ошибка при загрузке локации:', err.response?.data || err.message);
            setError('Не удалось загрузить локацию: ' + (err.response?.data?.message || err.message));
        }
        setLoading(false);
    };

    useEffect(() => {
        if (locationName) {
            fetchBarbers();
        }
    }, [locationName]);

    const fetchBarbers = async () => {
        setLoading(true);
        setError('');
        console.log('Starting fetchBarbers for locationName:', locationName);
        try {
            let response;
            const url = `/barbers/by-location?locationName=${encodeURIComponent(locationName)}`;
            console.log('Trying Request URL:', url);
            try {
                response = await api.get(url);
            } catch (err) {
                if (err.response?.status === 404) {
                    console.log('Falling back to /by-location');
                    const fallbackUrl = `/by-location?locationName=${encodeURIComponent(locationName)}`;
                    console.log('Trying Fallback URL:', fallbackUrl);
                    response = await api.get(fallbackUrl);
                } else {
                    throw err;
                }
            }
            console.log('Response data:', response.data);
            if (!Array.isArray(response.data) || response.data.length === 0) {
                setError('В этой локации нет барберов.');
            }
            setBarbers(response.data);
        } catch (err) {
            console.error('Ошибка при загрузке барберов:', err.response?.data || err.message);
            setError('Не удалось загрузить барберов: ' + (err.response?.data?.message || err.message));
        }
        setLoading(false);
        console.log('Finished fetchBarbers');
    };

    const handleSelectBarber = (barberId) => {
        console.log('Selected barberId:', barberId);
        navigate(`/book/offering/${barberId}`);
    };

    return (
        <Container className="py-5">
            <h2 className="text-center mb-4">Выберите барбера</h2>
            {error && <Alert variant="danger">{error}</Alert>}
            {loading ? (
                <div className="text-center">
                    <Spinner animation="border" />
                </div>
            ) : barbers.length > 0 ? (
                <Row xs={1} md={2} lg={3} className="g-4">
                    {barbers.map(barber => (
                        <Col key={barber.barberId}>
                            <Card className="card-item">
                                <Card.Body>
                                    <Card.Title>{barber.name}</Card.Title>
                                    <Card.Text>
                                        Услуги: {barber.offerings?.map(o => o.name).join(', ') || 'Не указаны'}
                                    </Card.Text>
                                    <Button
                                        variant="primary"
                                        className="btn-custom"
                                        onClick={() => handleSelectBarber(barber.barberId)}
                                    >
                                        Выбрать
                                    </Button>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>
            ) : (
                !error && <Alert variant="info">Нет доступных барберов.</Alert>
            )}
        </Container>
    );
};

export default BarberSelection;