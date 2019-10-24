require 'rest-client'
require 'json'
require_relative '../entities/ticket_entity.rb'
require_relative '../entities/accommodation_entity.rb'
require_relative '../entities/package_entity.rb'

# Classe que interage com servidor por meio do webservice ao definir
# as rotas para cada caso solicitado pelos menus.
module WebService
    URL = "localhost:8080/Server/resources"
    
    # Acessa a rota test do servidor
    def self.test
        begin
            response = RestClient.get("#{URL}/test")
        rescue RestClient::Exception
            return false
        rescue Errno::ECONNREFUSED
            return false
        end
        return true
    end

    # Acessa a rota tickets do servidor e retorna todas
    # as passagens
    def self.get_tickets
        begin
            response = RestClient.get("#{URL}/tickets")
        rescue RestClient::Exception => e
            print e.response
            raise e.message
        end
        
        tickets = parse_tickets_from_json(response.body)
        return tickets
    end

    # Acessa a rota accommodations e retorna todas as vagas
    # de hospedagem
    def self.get_accommodations
        begin
            response = RestClient.get("#{URL}/accommodations")
        rescue RestClient::Exception => e
            print e.response
            print e.message
        end

        accommodations = parse_accommodations_from_json(response.body)
        return accommodations
    end

    # Acessa a rota packages do servidor e retorna todos 
    # os pacotes
    def self.get_packages
        begin
            response = RestClient.get("#{URL}/packages")
        rescue RestClient::Exception => e
            print e.response
            raise e.message
        end

        packages = parse_packages_from_json(response.body)
        return packages
    end

    # Acessa a rota tickets/search para encontrar e retornar
    # todas as passagens que se adequam aos parametros recebidos
    def self.get_similar_tickets(ticket_type:, origin:, destination:,
        departure_date:, return_date:, num_people:)
        
        params = {
            ticketType: ticket_type,
            origin: origin,
            destination: destination,
            departureDate: departure_date.strftime('%Y-%m-%d'),
            numPeople: num_people
        }
        params[:returnDate] = return_date.strftime('%Y-%m-%d') if return_date

        begin
            response = RestClient.get("#{URL}/tickets/search", params: params)
        rescue RestClient::Exception => e
            print e.response
            raise e.message
        end

        tickets = parse_tickets_from_json(response.body)
        return tickets
    end

    # Acessa a rota accommodations/search para encontrar e retornar
    # todas as hospedagens que se adequam aos parametros recebidos
    def self.get_similar_accommodations(hotel_or_city_name:,
        entrance_date:, exit_date:, num_rooms:, num_people:)
        
        params = {
            hotelOrCityName: hotel_or_city_name,
            entranceDate: entrance_date.strftime('%Y-%m-%d'),
            exitDate: exit_date.strftime('%Y-%m-%d'),
            numRooms: num_rooms,
            numPeople: num_people
        }

        begin
            response = RestClient.get("#{URL}/accommodations/search", params: params)
        rescue RestClient::Exception => e
            print e.response
            raise e.message
        end

        accommodations = parse_accommodations_from_json(response.body)
        return accommodations
    end

    # Acessa a rota accommodations/search para encontrar e retornar
    # todas os pacotes que se adequam aos parametros recebidos
    def self.get_similar_packages(ticket_type:, origin:,
        hotel_or_city_name:, departure_date:, return_date:,
        num_rooms:, num_people:)

        params = {
            ticketType: ticket_type,
            origin: origin,
            hotelOrCityName: hotel_or_city_name,
            departureDate: departure_date.strftime('%Y-%m-%d'),
            returnDate: return_date.strftime('%Y-%m-%d'),
            numRooms: num_rooms,
            numPeople: num_people
        }

        begin
            response = RestClient.get("#{URL}/packages/search", params: params)
        rescue RestClient::Exception => e
            print e.response
            raise e.message
        end
        
        packages = parse_packages_from_json(response.body)
        return packages
    end

    # Publica uma nova passagem no servidor
    def self.publish_ticket(ticket)
        begin
            response = RestClient.post("#{URL}/tickets/new", ticket.json_converter, {content_type: :json})
        rescue RestClient::Exception => e
            print e.response
            raise e.message
        end

        return response
    end

    # Publica uma nova vaga de hospedagem no servidor
    def self.publish_accommodation(accommodation)
        begin
            response = RestClient.post("#{URL}/accommodations/new", accommodation.json_converter, {content_type: :json})
        rescue RestClient::Exception => e
            print e.response
            raise e.message
        end

        return response
    end

    # Publica um novo pacote no servidor
    def self.publish_package(package)
        begin
            response = RestClient.post("#{URL}/packages/new", package.json_converter, {content_type: :json})
        rescue RestClient::Exception => e
            print e.response
            raise e.message
        end

        return response
    end

    # Acessa a rota, de acordo com ticket_id, para comprar uma passagem
    def self.buy_ticket(ticket_id)
        begin
            response = RestClient.delete("#{URL}/tickets/buy/#{ticket_id}")
        rescue RestClient::Exception => e
            print e.response
            raise e.message
        end

        return response
    end

    # Acessa a rota, de acordo com accommodation_id, para comprar uma vaga de hospedagem
    def self.buy_accommodation(accommodation_id)
        begin
            response = RestClient.delete("#{URL}/accommodations/buy/#{accommodation_id}")
        rescue RestClient::Exception => e
            print e.response
            raise e.message
        end

        return response
    end

    # Acessa a rota, de acordo com package_id, para comprar um pacote
    def self.buy_package(package_id)
        begin
            response = RestClient.delete("#{URL}/packages/buy/#{package_id}")
        rescue RestClient::Exception => e
            print e.response
            raise e.message
        end

        return response
    end

    # Deleta uma passagem do servidor de acordo com o parametro recebido
    def self.delete_ticket(ticket_id)
        begin
            response = RestClient.delete("#{URL}/tickets/#{ticket_id}")
        rescue RestClient::Exception => e
            print e.response
            raise e.message
        end

        return response
    end

    # Deleta uma hospedagem do servidor de acordo com o parametro recebido
    def self.delete_accommodation(accommodation_id)
        begin
            response = RestClient.delete("#{URL}/accommodations/#{accommodation_id}")
        rescue RestClient::Exception => e
            print e.response
            raise e.message
        end

        return response
    end

    private

        # Transforma uma string JSON em um array de elementos 
        # de TicketEntity
        def self.parse_tickets_from_json(json_string)
            json_tickets = JSON.parse(json_string)

            tickets = []

            json_tickets.each do |json_ticket|
                tickets << parse_single_ticket(json_ticket)
            end

            return tickets
        end

        # Transforma um objeto JSON em um objeto TicketEntity
        def self.parse_single_ticket(json_ticket)
            id = json_ticket['id']
            ticket_type = json_ticket['ticketType']
            origin = json_ticket['origin']
            destination = json_ticket['destination']
            departure_date = Date.parse(json_ticket['departureDate'])
            return_date = json_ticket['returnDate'] ? Date.parse(json_ticket['returnDate']) : nil
            num_people = json_ticket['numPeople']
            price = json_ticket['price']

            return TicketEntity.new(
                ticket_id: id,
                ticket_type: ticket_type,
                origin: origin,
                destination: destination,
                departure_date: departure_date,
                return_date: return_date,
                num_people: num_people,
                price: price
            )
        end

        # Transforma uma string JSON em um array de elementos 
        # de AccommodationEntity
        def self.parse_accommodations_from_json(json_string)
            json_accommodations = JSON.parse(json_string)

            accommodations = []

            json_accommodations.each do |json_accommodation|
                accommodations << parse_single_accommodation(json_accommodation)
            end

            return accommodations
        end

        # Transforma um objeto JSON em um objeto AcccommodationEntity
        def self.parse_single_accommodation(json_accommodation)
            id = json_accommodation['id']
            location = json_accommodation['location']
            hotel_name = json_accommodation['hotel']
            entrance_date = Date.parse(json_accommodation['entranceDate'])
            exit_date = Date.parse(json_accommodation['exitDate'])
            num_rooms = json_accommodation['numRooms']
            num_people = json_accommodation['numPeople']
            price = json_accommodation['price']

            return AccommodationEntity.new(
                accommodation_id: id,
                location: location,
                hotel_name: hotel_name,
                entrance_date: entrance_date,
                exit_date: exit_date,
                num_rooms: num_rooms,
                num_people: num_people,
                price: price
            )
        end

        # Transforma uma string JSON em um array de elementos 
        # de PackageEntity
        def self.parse_packages_from_json(json_string)
            json_packages = JSON.parse(json_string)

            packages = []

            json_packages.each do |json_package|
                packages << parse_single_package(json_package)
            end

            return packages
        end

        def self.parse_single_package(json_package)
            id = json_package['id']
            ticket = parse_single_ticket(json_package['ticketEntity'])
            accommodation = parse_single_accommodation(json_package['accommodationEntity'])
            price = json_package['price']

            return PackageEntity.new(
                package_id: id,
                ticket_entity: ticket,
                accommodation_entity: accommodation,
                price:price
            )
        end

end