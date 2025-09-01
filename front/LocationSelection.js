import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Container, Row, Col, Card, Button, Spinner, Alert } from 'react-bootstrap';
import api from './api';
import './styles.css';

const LocationSelection = () => {
    const [locations, setLocations] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        fetchLocations();
    }, []);

    const fetchLocations = async () => {
        setLoading(true);
        setError('');
        try {
            const response = await api.get('/locations');
            console.log('Fetched locations:', response.data);
            const formattedLocations = Array.isArray(response.data)
                ? response.data.map(loc => {
                    if (loc && (loc.id || loc.locationId)) {
                        return {
                            id: loc.id || loc.locationId,
                            name: loc.name,
                            address: loc.address || 'Адрес не указан'
                        };
                    }
                    console.warn('Invalid location format:', loc);
                    return null;
                }).filter(loc => loc !== null)
                : [];
            if (formattedLocations.length === 0) {
                setError('Локации не найдены.');
            }
            setLocations(formattedLocations);
        } catch (error) {
            console.error('Ошибка при загрузке локаций:', error.response?.data || error.message);
            setError('Не удалось загрузить локации.');
        }
        setLoading(false);
    };

    const handleSelectLocation = (locationId) => {
        console.log('Selected locationId:', locationId);
        localStorage.setItem('locationId', locationId);
        navigate(`/book/barber/${locationId}`);
    };

    return (
        <Container className="py-5">
            <h2 className="text-center mb-4">Выберите локацию барбершопа</h2>
            {error && <Alert variant="danger">{error}</Alert>}
            {loading ? (
                <div className="text-center">
                    <Spinner animation="border" />
                </div>
            ) : locations.length > 0 ? (
                <Row xs={1} md={2} lg={3} className="g-4">
                    {locations.map(location => (
                        <Col key={location.id}>
                            <Card className="card-item">
                                <Card.Body>
                                    <Card.Title>{location.name}</Card.Title>
                                    <Card.Text>{location.address}</Card.Text>
                                    <Button
                                        variant="primary"
                                        className="btn-custom"
                                        onClick={() => handleSelectLocation(location.id)}
                                    >
                                        Выбрать
                                    </Button>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>
            ) : (
                <Alert variant="info">Нет доступных локаций.</Alert>
            )}
        </Container>
    );
};

export default LocationSelection;