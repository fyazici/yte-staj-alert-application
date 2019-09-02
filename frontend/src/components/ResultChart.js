import React, { Component } from 'react'
import axios from 'axios'
import Chart from 'react-google-charts';
import { Container } from 'react-bootstrap';
import SockJsClient from 'react-stomp';

class ResultChart extends Component {
    constructor(props) {
        super(props);
        this.state = {
            alertData: {},
            resultData: []
        };
    }

    SuccessChartHeader = [
        { type: "datetime", id: "timestamp", label: "Request Time" },
        { type: "number", id: "success", label: "Success" }
    ];

    ElapsedChartHeader = [
        { type: "datetime", id: "timestamp", label: "Request Time" },
        { type: "number", id: "elapsed", label: "RTT (ms)" }
    ];

    componentDidMount() {
        this.retrieveAlertData();
        this.retrieveChartData();
    }

    handleSocketData = (msg) => {
        this.setState({resultData: this.state.resultData.concat([msg])});
    }

    componentDidUpdate(prevProps, prevState) {
        if (this.props !== prevProps) {
            this.retrieveAlertData();
            this.retrieveChartData();
        }
    }

    retrieveAlertData = () => {
        axios.get(
            "http://localhost:8080/alert/" + this.props.alertId
        ).then((resp) => {
            this.setState({ alertData: resp.data });
        }).catch((err) => {
            console.error("Error on axios alert get: " + err);
        })
    }

    retrieveChartData = () => {
        if (this.props.alertId !== -1) {
            axios.get(
                "http://localhost:8080/results/" + this.props.alertId
            ).then((resp) => {
                this.setState({ resultData: this.state.resultData.concat(resp.data) });
            }).catch((err) => {
                console.error("Error on axios result get: " + err);
            });
        }
    }

    render() {
        return (
            <Container className="ResultChartBox">
                <SockJsClient url='http://localhost:8080/alertapplication-ws' topics={['/topic/' + this.props.alertId]}
                    onMessage={ (msg) => this.handleSocketData(msg) }
                    ref={ (client) => { this.clientRef = client }} />
                <h3><code>{this.state.alertData.alertName}</code> isimli alarm sonuçları:</h3>
                <Chart
                    chartType="ScatterChart"
                    loader={<div>Loading Chart</div>}
                    wi
                    data={
                        [this.SuccessChartHeader].concat(this.state.resultData.map((resultElem) => { 
                            return [new Date(resultElem.timestamp), resultElem.success ? 1 : 0]
                        }))
                    }
                    options={{
                        chartArea: { width: "75%" },
                        chart: {
                            title: "Request Results",
                        },
                        vAxis: {
                            minValue: 0,
                            maxValue: 1
                        }
                    }}
                />
                <br />
                <Chart
                    chartType="ScatterChart"
                    loader={<div>Loading Chart</div>}
                    data={
                        [this.ElapsedChartHeader].concat(this.state.resultData.map((resultElem) => { 
                            return [new Date(resultElem.timestamp), resultElem.elapsed]
                        }))
                    }
                    options={{
                        chartArea: { width: "75%" },
                        chart: {
                            title: "Request Results",
                        },
                        vAxis: {
                            minValue: 0,
                            maxValue: 1000
                        }
                    }}
                />
            </Container>
        );
    }
}

export default ResultChart;