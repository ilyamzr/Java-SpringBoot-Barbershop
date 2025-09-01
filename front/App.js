import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes, Link, useNavigate } from 'react-router-dom';
import { Button, Container, Navbar, Nav } from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import LocationSelection from './LocationSelection';
import BarberSelection from './BarberSelection';
import OfferingSelection from './OfferingSelection';
import TimeSelection from './TimeSelection';
import BarberList from './BarberList';
import OfferingList from './OfferingList';
import OrderList from './OrderList';
import UserList from './UserList';
import LocationList from './LocationList';
import Profile from './Profile';
import './styles.css';

function App() {
  const [theme, setTheme] = useState(localStorage.getItem('theme') || 'light');

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('theme', theme);
  }, [theme]);

  const toggleTheme = () => {
    setTheme(theme === 'light' ? 'dark' : 'light');
  };

  return (
      <Router>
        <Navbar expand="lg" className="navbar-custom">
          <Container fluid>
            <Navbar.Brand as={Link} to="/">
              <strong>BarberShop Manager</strong>
            </Navbar.Brand>
            <Navbar.Toggle aria-controls="basic-navbar-nav" />
            <Navbar.Collapse id="basic-navbar-nav">
              <Nav className="me-auto d-flex flex-row">
                <Nav.Link as={Link} to="/barbers" className="nav-link-custom mx-2">
                  <strong>Барберы</strong>
                </Nav.Link>
                <Nav.Link as={Link} to="/offerings" className="nav-link-custom mx-2">
                  <strong>Услуги</strong>
                </Nav.Link>
                <Nav.Link as={Link} to="/orders" className="nav-link-custom mx-2">
                  <strong>Заказы</strong>
                </Nav.Link>
                <Nav.Link as={Link} to="/users" className="nav-link-custom mx-2">
                  <strong>Пользователи</strong>
                </Nav.Link>
                <Nav.Link as={Link} to="/locations" className="nav-link-custom mx-2">
                  <strong>Локации</strong>
                </Nav.Link>
                <Nav.Link as={Link} to="/profile" className="nav-link-custom mx-2">
                  <strong>Профиль</strong>
                </Nav.Link>
              </Nav>
              <Button variant="primary" size="sm" className="btn-custom" onClick={toggleTheme}>
                {theme === 'light' ? '🌙' : '☀️'}
              </Button>
            </Navbar.Collapse>
          </Container>
        </Navbar>
        <Container fluid className="mt-5">
          <Routes>
            <Route
                path="/"
                element={
                  <div className="home-page text-center">
                    <h1 className="display-4 mb-4">Добро пожаловать в наш барбершоп!</h1>
                    <p className="lead mb-5">
                      Запишитесь на стрижку и получите стиль, который подчеркнет вашу индивидуальность!
                    </p>
                    <Button
                        as={Link}
                        to="/book/location"
                        variant="primary"
                        size="lg"
                        className="btn-custom"
                    >
                      Записаться на стрижку
                    </Button>
                  </div>
                }
            />
            <Route path="/book/location" element={<LocationSelection />} />
            <Route path="/book/barber/:locationId" element={<BarberSelection />} />
            <Route path="/book/offering/:barberId" element={<OfferingSelection />} />
            <Route path="/book/time/:barberId/:offeringId" element={<TimeSelection />} />
            <Route path="/barbers" element={<BarberList />} />
            <Route path="/offerings" element={<OfferingList />} />
            <Route path="/orders" element={<OrderList />} />
            <Route path="/users" element={<UserList />} />
            <Route path="/locations" element={<LocationList />} />
            <Route path="/profile" element={<Profile />} />
          </Routes>
        </Container>
      </Router>
  );
}

export default App;