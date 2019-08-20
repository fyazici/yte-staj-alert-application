import React from 'react';
import './App.css';

import AlertInput from "./components/AlertInput";
import AlertList from "./components/AlertList";
import ResultChart from './components/ResultChart';

import { BrowserRouter as Router, Route } from "react-router-dom";
import { Navbar, Nav } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap'

function AlertInputPage() {
  return <AlertInput />;
}

function AlertListPage() {
  return <AlertList />;
}

function AlertResultsPage({ match }) {
  return <ResultChart alertId={match.params.alertId} />;
}

function AppRouter() {
  return (
    <Router>
      <Navbar bg="dark" variant="dark">
        <Navbar.Brand>
          <LinkContainer to="/">
            <Nav.Link>Alarm Uygulaması</Nav.Link>
          </LinkContainer>
        </Navbar.Brand>
        <Nav className="mr-auto">
          <LinkContainer to="/newAlert">
            <Nav.Link>Alarm Ekle</Nav.Link>
          </LinkContainer>
          <LinkContainer to="/alerts">
            <Nav.Link>Alarm Listesi</Nav.Link>
          </LinkContainer>
        </Nav>
      </Navbar>

      <Route exact path="/newAlert" component={AlertInputPage} />
      <Route exact path="/alerts" component={AlertListPage} />
      <Route path="/alerts/:alertId" component={AlertResultsPage} />
    </Router>
  )
}

export default AppRouter;
