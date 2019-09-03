import React from 'react';
import './App.css';

import AlertInput from "./components/AlertInput";
import AlertList from "./components/AlertList";
import ResultChart from './components/ResultChart';

import { BrowserRouter as Router, Route } from "react-router-dom";
import { Navbar, Nav, ToggleButtonGroup, ToggleButton } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';

import { withTranslation } from 'react-i18next';

function AlertInputPage() {
  return <AlertInput />;
}

function AlertListPage() {
  return <AlertList />;
}

function AlertResultsPage({ match }) {
  return <ResultChart alertId={match.params.alertId} />;
}

function AppRouter({ t, i18n }) {
  return (
    <Router>
      <Navbar bg="dark" variant="dark">
        <Navbar.Brand>
          <LinkContainer to="/">
            <Nav.Link>{t('app.nav.brand')}</Nav.Link>
          </LinkContainer>
        </Navbar.Brand>
        <Nav className="mr-auto">
          <LinkContainer to="/newAlert">
            <Nav.Link>{t('app.nav.add-alert')}</Nav.Link>
          </LinkContainer>
          <LinkContainer to="/alerts">
            <Nav.Link>{t('app.nav.list-alerts')}</Nav.Link>
          </LinkContainer>
        </Nav>
          <ToggleButtonGroup type="radio" value={i18n.language} name="language-select" onChange={(val) => {
            i18n.changeLanguage(val);
          }}>
            <ToggleButton value={"en"}>EN</ToggleButton>
            <ToggleButton value={"tr"}>TR</ToggleButton>
          </ToggleButtonGroup>
      </Navbar>
      <Route exact path="/newAlert" component={AlertInputPage} />
      <Route exact path="/alerts" component={AlertListPage} />
      <Route path="/alerts/:alertId" component={AlertResultsPage} />
    </Router>
  )
}

export default withTranslation()(AppRouter);
